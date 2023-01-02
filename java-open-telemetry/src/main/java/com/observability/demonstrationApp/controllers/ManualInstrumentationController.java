package com.observability.demonstrationApp.controllers;

import io.opentelemetry.api.common.AttributeKey;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.GlobalOpenTelemetry;

/**
 * A rest controller used to demonstrate Open Telemetry's manual
 * capabilities.
 */
@RestController
public class ManualInstrumentationController {
    private final Log log = LogFactory.getLog(ManualInstrumentationController.class);

    private OpenTelemetry openTelemetry;
    LongCounter counter;

    /**
     * Initialize OpenTelemetry. This code was taken from
     * https://opentelemetry.io/docs/instrumentation/java/manual/
     */
    public ManualInstrumentationController() {
        Meter meter =
                GlobalOpenTelemetry.meterBuilder("aws-otel").setInstrumentationVersion("1.0").build();

        counter = meter.counterBuilder("demonstration_metric")
                .setDescription("A metric that represents nothing")
                .setUnit("count")
                .build();
    }

    @RequestMapping("/manually-instrument-metrics/")
    public Map<String, String> manuallyInstrumented() {

        log.info("Exercising tracing manual instrumentation");

        Attributes attributes = Attributes.of(AttributeKey.stringKey("Key"), "SomeWork");

        counter.add(1000, attributes);

        HashMap<String, String> responseMap = new HashMap<>();
        responseMap.put("foo", "bar");

        return responseMap;
    }

}
