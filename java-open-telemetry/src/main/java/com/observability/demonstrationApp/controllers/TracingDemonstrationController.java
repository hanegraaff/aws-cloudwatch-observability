package com.observability.demonstrationApp.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.GetMapping;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import java.util.List;


import java.net.URL;
import java.util.Scanner;


@RestController
public class TracingDemonstrationController {
    @RequestMapping("/goto/{url}")
    public String processSingleURL(@PathVariable String url) throws Exception {


        try {
            String content = downloadWebData("https://" + url);
            execS3APIs(url);
            return content;
        } catch (Exception e) {
            return "failed to process, because: " + e;
        }
    }

    @GetMapping("/gotoall/")
    public String processMultipleURLs() throws Exception {
        try {
            downloadWebData("https://amazon.com");
            downloadWebData("https://google.com");
            downloadWebData("https://doesnotexist-12345.com");
            downloadWebData("https://localhost.com");

        } catch (Exception e) {
            return "failed to process, because: " + e;
        }

        return "Done!";
    }


    @WithSpan
    private String downloadWebData(@SpanAttribute("webAddress") String webAddress)
            throws Exception {

        URL url = new URL(webAddress);
        Scanner sc = new Scanner(url.openStream());

        StringBuffer sb = new StringBuffer();
        while (sc.hasNext()) {
            sb.append(sc.next());
        }
        return sb.toString();
    }

    @WithSpan
    private void execS3APIs(@SpanAttribute("webAddress") String webAddress) {

        // S3
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();

        List<Bucket> buckets = s3.listBuckets();
        System.out.println("Your {S3} buckets are:");
        for (Bucket b : buckets) {
            System.out.println("* " + b.getName());
        }

        //EC2
    }
}
