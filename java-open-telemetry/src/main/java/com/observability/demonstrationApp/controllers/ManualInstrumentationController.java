package com.observability.demonstrationApp.controllers;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.metrics.DoubleHistogram;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.GlobalOpenTelemetry;

/**
 * A rest controller used to demonstrate Open Telemetry's metric generation
 * capabilities.
 */
@RestController
public class ManualInstrumentationController {
    private final Log log = LogFactory.getLog(ManualInstrumentationController.class);

    DoubleHistogram transactionHistogram;
    DoubleHistogram responseTimeHistogram;
    DoubleHistogram temperatureHistogram;
    Random rand;

    // list of possible values used for Transaction Histogram
    final List<String> paymentTypes = Arrays.asList("cash", "credit", "crypto", "check");
    final List<String> productCategories = Arrays.asList("computer", "car", "motorcycle", "food", "clothes", "mystery-box");

    // list of possible values used for Response Time Histogram
    final List<String> serviceList = Arrays.asList("price", "inventory", "recommendation", "cart");
    final List<String> activityList = Arrays.asList("download", "upload", "sideload");

    // list of possible values used for temperature Histogram
    final List<String> location = Arrays.asList("warehouse", "office", "outside", "garage", "sun-surface");


    /**
     * Initialize OpenTelemetry. This code was taken from
     * https://opentelemetry.io/docs/instrumentation/java/manual/
     */
    public ManualInstrumentationController() {
        rand = new Random();

        Meter meter =
                GlobalOpenTelemetry.meterBuilder("aws-otel").setInstrumentationVersion("1.0").build();

        transactionHistogram = meter.histogramBuilder("transaction_count")
                .setDescription("A Metric that represents a transaction count")
                .setUnit("count")
                .build();

        responseTimeHistogram = meter.histogramBuilder("response_time")
                .setDescription("A Metric that represents a response time")
                .setUnit("Milliseconds")
                .build();

        temperatureHistogram = meter.histogramBuilder("temperature")
                .setDescription("A Metric that represents the temperature of something")
                .setUnit("farenheit")
                .build();
    }

    @RequestMapping("/business-metrics/")
    public Map<String, String> manuallyInstrumented() {

        log.info("");

        Attributes transactionAttributes
                = Attributes.of(AttributeKey.stringKey("PaymentType"), paymentTypes.get(rand.nextInt(paymentTypes.size())),
                AttributeKey.stringKey("ProductCategory"), productCategories.get(rand.nextInt(productCategories.size()))
        );

        Double transactions = rand.nextDouble(1000);

        transactionHistogram.record(transactions, transactionAttributes);

        HashMap<String, String> responseMap = new HashMap<>();
        responseMap.put("transactions", transactions.toString());
        responseMap.put("Attributes", transactionAttributes.toString());

        return responseMap;
    }


    /**
     * Runs every 10 seconds and published random performance metrics.
     */
    @Scheduled(fixedRate = 10 * 1000)
    public void runOnTimer(){
        log.info("Publishing Performance Metrics");
        Attributes responseTimeAttributes =
                Attributes.of(AttributeKey.stringKey("ServiceName"), serviceList.get(rand.nextInt(serviceList.size())),
                AttributeKey.stringKey("Activity"), activityList.get(rand.nextInt(activityList.size()))
        );
        responseTimeHistogram.record(rand.nextDouble(125), responseTimeAttributes);


        Attributes temperatureAttributes =
                Attributes.of(AttributeKey.stringKey("Location"), location.get(rand.nextInt(location.size()))
                );
        temperatureHistogram.record(rand.nextInt(212), temperatureAttributes);

    }

}
