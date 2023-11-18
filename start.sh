#!/bin/bash

docker-compose down
docker network create flink-network
docker-compose build
docker-compose up