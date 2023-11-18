#!/bin/bash

curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" debezium:8083/connectors/ -d '{
  "name": "my-postgres-connector",
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    "tasks.max": "1",
    "database.hostname": "postgresql",
    "database.port": "5432",
    "database.user": "demir",
    "database.password": "demir*/895/",
    "database.dbname": "postgres",
    "database.server.name": "postgresql",
    "table.whitelist": "public.debezium_connector_table",
    "topic.prefix":"debezium_connector_table",
    "slot.name": "debezium_slot",
    "publication.name": "my-publication",
    "key.converter": "org.apache.kafka.connect.storage.StringConverter",
    "value.converter": "org.apache.kafka.connect.json.JsonConverter",
    "internal.key.converter": "org.apache.kafka.connect.json.JsonConverter",
    "internal.value.converter": "org.apache.kafka.connect.json.JsonConverter",
    "connect.key.converter": "org.apache.kafka.connect.json.JsonConverter",
    "connect.value.converter": "org.apache.kafka.connect.json.JsonConverter",
    "connect.internal.key.converter": "org.apache.kafka.connect.json.JsonConverter",
    "connect.internal.value.converter": "org.apache.kafka.connect.json.JsonConverter"
  }
}'