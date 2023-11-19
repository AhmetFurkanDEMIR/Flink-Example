package org.example
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.connector.kafka.sink.{KafkaRecordSerializationSchema, KafkaSink}
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import org.apache.flink.streaming.api.scala._
import io.circe.Json
import io.circe.parser._

import org.apache.flink.api.common.functions.FlatMapFunction
import org.apache.flink.util.Collector

object App{

  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val kafkaSource = KafkaSource.builder()
      .setBootstrapServers("kafka:9092")
      .setTopics("debezium_connector_table.public.trendyol_product")
      .setGroupId("demir-consumer")
      .setStartingOffsets(OffsetsInitializer.latest())
      .setValueOnlyDeserializer(new SimpleStringSchema())
      .build()

    val lines = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "Kafka Source")

    val processedData: DataStream[Json] = lines.flatMap(new FlatMapFunction[String, Json] {
      override def flatMap(value: String, out: Collector[Json]): Unit = {
        for {
          json <- parse(value).toOption
          afterJson <- json.hcursor.downField("payload").downField("after").as[Json].toOption
        } yield out.collect(afterJson)
      }
    })

    val serializer = KafkaRecordSerializationSchema.builder()
      .setValueSerializationSchema(new SimpleStringSchema())
      .setTopic("debezium_connector_table.public.trendyol_product-clear-out")
      .build()

    // Adding KafkaSink
    val kafkaSink = KafkaSink.builder()
      .setBootstrapServers("kafka:9092")
      .setRecordSerializer(serializer)
      .build()

    val stringDataStream: DataStream[String] = processedData.map(json => json.noSpaces)

    stringDataStream.sinkTo(kafkaSink)

    env.execute("Flink Kafka Example")

  }
}