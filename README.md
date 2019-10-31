# bacnet-azure-iot
Push BacNet object to Azure IoT


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

## Environment Variables
DEVICE_CONNECTION_STRING
NUMBER_REQUESTS

## Dependencies
Currently I'm using a GPLv3 dependency in bacnet4j and bacnet4jwrapper.
The license in this repo is breaking those licenses. The way forward will be to either
move this repo to GPLv3, or remove bacne4j dependencies.