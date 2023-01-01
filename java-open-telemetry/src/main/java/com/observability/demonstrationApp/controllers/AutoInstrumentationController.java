package com.observability.demonstrationApp.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.*;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


@RestController
public class TracingDemonstrationController {

    private final Log log = LogFactory.getLog(TracingDemonstrationController.class);

    @RequestMapping("/connect-to-all/")
    @WithSpan()
    public Map<String, String> autoInstrumented() {

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
        log.info("reading from: " + webAddress);
        try{

            URL url = new URL(webAddress);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while (in.readLine() != null);
            in.close();

            return "SUCCESS";
        }
        catch (Exception e){
            log.error("error reading website: " + e.getMessage());
            return e.getMessage();
        }
    }

    private String execS3APIs() {
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();

        try{
            List<Bucket> buckets = s3.listBuckets();

            log.info("Your {S3} buckets are:");
            for (Bucket b : buckets) {
                log.info("* " + b.getName());
            }

            return "SUCCESS";
        }
        catch(Exception e){
            return e.getMessage();
        }
    }
}
