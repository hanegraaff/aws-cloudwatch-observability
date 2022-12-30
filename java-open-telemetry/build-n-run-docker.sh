#!/bin/sh

mvn package

docker build -f dockerfile -t demoapp/demo-app:v1.0.0 .

docker compose up
#docker run -p 8080:8080 \
#  -e AWS_REGION=us-east-1 -e AWS_PROFILE=default \
#  -e OTEL_JAVAAGENT_CONFIGURATION_FILE=open-telemetry-java-agent.properties \
#  -v ~/.aws:/root/.aws demoapp/demo-app:v1.0.0