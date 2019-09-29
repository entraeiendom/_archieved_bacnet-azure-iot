#!/usr/bin/env bash
#docker run -it --name=bacnet-azure-iot-java <your docker user>/bacnet-azure-iot-java:alpine /bin/bash
DEVICE_CONNECTION_STRING=connection_to_azure_iot
NUMBER_REQUESTS=10
docker run --name=bacnet-azure-iot-java -e DEVICE_CONNECTION_STRING=$DEVICE_CONNECTION_STRING -e NUMBER_REQUESTS=$NUMBER_REQUESTS <your docker user>/bacnet-azure-iot-java:alpine
