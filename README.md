# AWS Native Observability

This repo contains 3 sample "observable" applications whose instrumentation is based on AWS CloudWatch.

1. A Java based Spring Boot application instrumented with OpenTelemetry. This can be run as a standalone Java application or a Docker container.

2. A Python based Lambda function instrumented with the `aws-embedded-metrics` module and the AWS XRay SDK.

3. (TBD) A Java based Spring Boot application instrumented with Micrometer.

The purpose of this project is to provide working examples using popular frameworks.