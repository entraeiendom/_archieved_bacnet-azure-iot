# bacnet-azure-iot
Push BacNet object to Azure IoT


## Development

### Build
`mvn clean build`

### Create bundle
'mvn clean package'

### Verify

'java -jar target/bacnetagent-{version}.jar "{connection string}" "{number of requests to send}" "{https or amqps or mqtt or amqps_ws }" 

