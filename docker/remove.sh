#!/usr/bin/env bash
echo stopping bacnet-azure-iot-java
docker stop bacnet-azure-iot-java
echo removing bacnet-azure-iot-java
docker rm bacnet-azure-iot-java
echo list active docker containers
docker ps
