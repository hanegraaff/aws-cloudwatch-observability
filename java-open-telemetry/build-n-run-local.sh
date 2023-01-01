#!/bin/sh

mvn clean
mvn package

cd target

curl -LJO -o aws-opentelemetry-agent.jar github.com/aws-observability/aws-otel-java-instrumentation/releases/download/v1.20.0/aws-opentelemetry-agent.jar

OTEL_RESOURCE_ATTRIBUTES=service.name=demonstrationApp-local,service.namespace=com.hanegraaff.observability,service.version=1.0.0 java -javaagent:aws-opentelemetry-agent.jar \
     -jar demonstrationApp-1.0.0.jar