# Open Telemetry Spring Boot Sample Application
This repo contains a Sample Spring Boot Application instrumented with Open Telemetry. The application can be run as a standard Java application or using a docker container.

## What does the app do?
When running the app, Spring's Embedded Tomcat will listen on Port `8080` (http://localhost:8080) and expose the following URL mappings:

* `http://localhost:8080/auto-instrument-traces/`
* `http://localhost:8080/business-metrics/` 


## Running the sample app as a standalone Java App

## Running the sample app as a standalone Java App

1. Run the AWS OTEL Collector. The easiest way is to do so using in a Docker container:

    ```cmd
    >> docker pull amazon/aws-otel-collector

    >> docker run -p 4317:4317 -p 55680:55680 -p 8889:8888 \
      -e AWS_REGION=us-east-1 \
      -e AWS_PROFILE=default \
      -v ~/.aws:/root/.aws \
      amazon/aws-otel-collector
    ```

2. Build and run the sample application.

    ```
    >> build-n-run-local.sh
    ```

