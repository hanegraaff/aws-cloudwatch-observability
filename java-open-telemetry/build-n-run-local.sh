#!/bin/sh

mvn package
cd target

OTEL_RESOURCE_ATTRIBUTES=service.name=demonstrationApp,service.namespace=com.hanegraaff.observability java -javaagent:aws-opentelemetry-agent-1.20.0.jar \
     -jar demonstrationApp-0.0.1-SNAPSHOT.jar