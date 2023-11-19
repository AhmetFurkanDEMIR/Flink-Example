#!/bin/bash

wget --directory-prefix=./flink-jar/ https://github.com/AhmetFurkanDEMIR/Flink-Example/releases/download/jar/untitled-1.0-SNAPSHOT-jar-with-dependencies.jar

docker-compose down
docker network create flink-network
docker-compose build
docker-compose up