![](https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white) ![](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white) ![](https://img.shields.io/badge/Scala-DC322F?style=for-the-badge&logo=scala&logoColor=white) ![](https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white)

# Flink Example

![](/images/schema.png)

Project to create flink data architecture with products previously taken from [Trendyol.com](https://www.trendyol.com/) e-commerce site.

Dataset link: [https://www.kaggle.com/datasets/ahmetfurkandemr/trendyol-product-comments](https://www.kaggle.com/datasets/ahmetfurkandemr/trendyol-product-comments)

[**Apache Flink**](https://flink.apache.org/) is an open-source, unified stream-processing and batch-processing framework developed by the Apache Software Foundation. The core of Apache Flink is a distributed streaming data-flow engine written in Java and Scala. Flink executes arbitrary dataflow programs in a data-parallel and pipelined (hence task parallel) manner. Flink's pipelined runtime system enables the execution of bulk/batch and stream processing programs. Furthermore, Flink's runtime supports the execution of iterative algorithms natively.

## **Run the project**

First, it is necessary to start the docker services by running the start.sh file.

```bash
sudo bash start.sh
```

After running this command, all services will start. The services automatically connect and data is entered into the postgresql database, then the data is written to the kafka topic with debezium.

* **PostgreSQL | postgresql:5432 or 0.0.0.0:5432**

    - POSTGRES_DB=postgres
    - POSTGRES_USER=demir
    - POSTGRES_PASSWORD=demir*/895/


* **Pgadmin | [0.0.0.0:8888](http://0.0.0.0:8888)**

    - PGADMIN_DEFAULT_EMAIL: demir@ahmetfurkandemir.com
    - PGADMIN_DEFAULT_PASSWORD: demir*/895/

    ![](/images/pgadmin.png)


* **Debezium Ui | [0.0.0.0:8086](http://0.0.0.0:8086/)**

    ![](/images/debezium_ui.png)


* **Kafka Ui | [0.0.0.0:8080](http://0.0.0.0:8080)**

    ![](/images/kafka_ui.png)

    - If you do not receive any errors, you can see that data is flowing to [debezium_connector_table.public.trendyol_product](http://0.0.0.0:8080/ui/clusters/kafka/all-topics/debezium_connector_table.public.trendyol_product/messages?keySerde=String&valueSerde=String&limit=100) topic by debezium.

### **Flink Run**

**Flink Ui | [0.0.0.0:8081](http://0.0.0.0:8081)**

![](/images/flink_ui.png)

Flink starts up automatically like other services, but we need to make some configurations in Flink.

First, download the jar file from the link below to the [/flink-jar](/flink-jar/) folder.

Link: [https://github.com/AhmetFurkanDEMIR/Flink-Example/releases/download/jar/untitled-1.0-SNAPSHOT-jar-with-dependencies.jar](https://github.com/AhmetFurkanDEMIR/Flink-Example/releases/download/jar/untitled-1.0-SNAPSHOT-jar-with-dependencies.jar)

This .jar file reads the data in the kafka topic and simplifies this data, makes it useful and prints it on a new topic. [source codes of the file](/flink-scala-source/).

Find the container address of Flink jobmanager with the following command.

```bash
docker ps
```

And again, enter the container with the following command.

```bash
docker exec -it xxxxxx /bin/bash
```

```bash
flink run -c org.example.App /flink-example/untitled-1.0-SNAPSHOT-jar-with-dependencies.jar
```

If the command runs successfully, you can see the job running via Flink Ui as shown in the picture below. And never close this window. [Check link](http://0.0.0.0:8081/#/job/running)

![](/images/flink_run_jar.png)

After the job runs without errors, you need to insert the data in the sql file into the database to ensure data flow. [SQL file](/sql/insert_data.sql)

```bash
psql -h 0.0.0.0 -d postgres -U demir -f sql/insert_data.sql
```

After the data is inserted, you can see that a new topic named [debezium_connector_table.public.trendyol_product-clear-out](http://0.0.0.0:8080/ui/clusters/kafka/all-topics/debezium_connector_table.public.trendyol_product-clear-out/messages?keySerde=String&valueSerde=String&limit=100) has been opened on Kafka.

After these operations, open a new terminal and enter the Flink Jobmanager container, which is the process you did before.

```bash
docker exec -it xxxxxx /bin/bash
```

Then run Flink SQL Client.

```bash
./bin/sql-client.sh -j /flink-example/flink-sql-connector-kafka-3.0.1-1.18.jar
```

If everything is OK, you should connect to a command interface like the one below.

![](/images/flink_sql_client.png)

Create a table from the kafka topic with the following command in Flink SQL.

```sql
CREATE TABLE trendyol_product_flink_table(
  `Product_Id` STRING,
  `Product_Name` STRING,
  `Product_Brand` STRING,
  `Product_Link` STRING
) WITH (
  'connector' = 'kafka',
  'topic' = 'debezium_connector_table.public.trendyol_product-clear-out',
  'properties.bootstrap.servers' = 'kafka:9092',
  'properties.group.id' = 'demir-flink-sql',
  'scan.startup.mode' = 'earliest-offset',
  'format' = 'json'
);
```

And finally, run an SQL query on this table and watch with pleasure as your data flows :)

```sql
select * from trendyol_product_flink_table;
```

![](/images/sql_1.png)

![](/images/sql_2.png)

Check the jobs and SQL commands you run from the interface.

![](/images/flink_run_jobs.png)

[**Ahmet Furkan Demir**](https://ahmetfurkandemir.com/)