# bacnet-azure-iot
Push BacNet object to Azure IoT

## Deamon to run on Raspberry Pi

[BacnetAgentDeamon](src/main/java/no/entra/rec/bacnetagent/BacnetAgentDeamon.java)

###### Config

DEVICE_CONNECTION_STRING
```
az iot hub device-identity show-connection-string --device-id {your IoT Device name} --hub-name entra-edge-dev-hub
 {your IoT Hub name}
```
NUMBER_REQUESTS -> temporary 

## Manually read messages from IoT Hub

[ReadDeviceToCloudMessages](src/main/java/no/entra/rec/bacnetagent/ReadDeviceToCloudMessages.java)

Connect to Azure IoT and read messages using AMQP. The massages are read from the built-in Event Hub-compatible messaging endpoint messages/events.

###### Config

EVENT_HUB_ENDPOINT
```
az iot hub show --query properties.eventHubEndpoints.events.endpoint --name {your IoT Hub name}
```
EVENT_HUB_PATH
```
az iot hub show --query properties.eventHubEndpoints.events.path --name {your IoT Hub name}
```

IOT_SERVICE_PRIMARY_KEY
```
az iot hub policy show --name service --query primaryKey --hub-name {your IoT Hub name}
```

## Manually send RealEstateCore observations and events IoT Hub

[SendDeviceToCloudMessage](src/main/java/no/entra/rec/bacnetagent/SendDeviceToCloudMessage.java)

Connect to Azure IoT and send messages using MQTT. 

###### Config

DEVICE_CONNECTION_STRING
```
az iot hub device-identity show-connection-string --device-id {your IoT Device name} --hub-name entra-edge-dev-hub
 {your IoT Hub name}
```

## Development

### Build
`mvn clean build`

### Create bundle
`mvn clean package`

### Verify

`java -jar target/bacnetagent-{version}.jar "{connection string}" "{number of requests to send}" "{https or amqps or mqtt or amqps_ws }"` 


## Docker

### Docker on Ubuntu aka amd64 processors

```
docker/build-alpine-amd64.sh
docker/run-alpine.sh "DeviceConnectionString"
``` 

## RealEstateCore mesages

[RealEstateCore example message](https://github.com/RealEstateCore/rec/blob/master/edge_messages/edge_message_example.json)

