# kotlin-spring-protobuf
Demo project to show the integration of these three technologies

## Initial setup
For the first execution the protobuf file(s) need to be compiled to the target language (Java in this case).
Executing the following command in the project root folder is sufficient:
````text
./gradlew build
````
The protobuf classes will be generated first, followed by the kotlin sources.

## Communicating with the REST endpoint
For quick demonstrations without an explicit client cURL can be used to communicate with the endpoint.

``protoc --decode`` returns the result of the call in the protobuf text format.

### Add new event
````text
cat src/test/resources/proto.txt \
  | protoc --encode "model.DeploymentEvent" src/main/proto/deployment-event.proto \
  | curl -H "Content-Type: application/x-protobuf" -sS -XPOST http://localhost:4242/api/deployments --data-binary @- \
  | protoc --decode "model.DeploymentEvent" src/main/proto/deployment-event.proto

id: 4
target: ACCEPTANCE
technology: "Database"
product: "Oracle11"
version: "0.0.1"
status: FAILED
````

### Get event
````text
curl -XGET http://localhost:4242/api/deployments/4 \
  | protoc --decode "model.DeploymentEvent" src/main/proto/deployment-event.proto

id: 4
target: ACCEPTANCE
technology: "Database"
product: "Oracle11"
version: "0.0.1"
status: FAILED
````

### Find by target
````text
curl -XGET "http://localhost:4242/api/deployments?target=ACCEPTANCE" \
  | protoc--decode "model.DeploymentEvents" src/main/proto/deployment-event.proto

deploymentEvent {
  id: 2
  target: ACCEPTANCE
  technology: "Database"
  product: "H2"
  version: "0.0.1"
  status: FAILED
}
deploymentEvent {
  id: 4
  target: ACCEPTANCE
  technology: "Database"
  product: "Oracle11"
  version: "0.0.1"
  status: FAILED
}
````
