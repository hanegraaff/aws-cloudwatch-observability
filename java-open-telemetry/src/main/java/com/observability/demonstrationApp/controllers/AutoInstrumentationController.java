package com.observability.demonstrationApp.controllers;

import com.observability.demonstrationApp.support.Connector;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * A rest controller used to demonstrate Open Telemetry's auto-instrumentation
 * capabilities.
 */
@RestController
public class AutoInstrumentationController {
    private final Log log = LogFactory.getLog(AutoInstrumentationController.class);

    /**
     * Request handler used to exercise the automatic tracing capabilities
     * It will navidate to various URLs and then exercise an S3 API
     * @return
     */
    @RequestMapping("/auto-instrument-traces/")
    public Map<String, String> autoInstrumented() {

        log.info("Exercising tracing auto-instrumentation");

        HashMap<String, String> responseMap = new HashMap<>();
        List<String> websiteList = new ArrayList<>();

        websiteList.add("https://amazon.com");
        websiteList.add("https://google.com");
        websiteList.add("https://doesnotexist-12345.com");
        websiteList.add("https://localhost.com");

        for (String nextWebsite : websiteList){
            responseMap.put(nextWebsite, readWebData(nextWebsite));
        }

        responseMap.put("S3 APIs", execS3APIs());

        return responseMap;
    }

    @WithSpan()
    private String readWebData(@SpanAttribute("webAddress") String webAddress)
    {
        return Connector.readHTTPData(webAddress);
    }

    @WithSpan()
    private String execS3APIs() {
        return Connector.execS3APIs();
    }
}
