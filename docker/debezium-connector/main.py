import requests
import time

time.sleep(50)

url = "http://debezium:8083/connectors/"
headers = {
    "Accept": "application/json",
    "Content-Type": "application/json"
}

data = {
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
        "table.whitelist": "public.trendyol_product",
        "topic.prefix": "debezium_connector_table",
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
}

# JSON verisi ile POST isteği gönderme
response = requests.post(url, headers=headers, json=data)

# Yanıtı kontrol etme
if response.status_code == 200:
    print("İstek başarılı!")
    print(response.json())  # Yanıttan JSON verisi almak için
else:
    print(f"Hata: {response.status_code} - {response.text}")