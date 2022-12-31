#!/bin/sh

mvn clean
mvn package
docker build -f dockerfile -t demoapp/demo-app:v1.0.0 .
docker compose up