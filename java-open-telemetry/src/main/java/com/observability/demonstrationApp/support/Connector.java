package com.observability.demonstrationApp.support;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.observability.demonstrationApp.controllers.AutoInstrumentationController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * A General connector class used to generate traffic that will be captured by Open Telemetry
 */
public class Connector {
    private static final Log log = LogFactory.getLog(AutoInstrumentationController.class);

    /**
     * Reads the content of a URL and returns a string whether the operation
     * was successful or not.
     *
     * @param webAddress The target URL
     * @return a String set to either "SUCCESS" or the contents of the exception in case of an error
     */
    public static String readHTTPData(String webAddress)
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

    /**
     * Exercises the S3 Service, by calling the "ListBuckets" API
     * @return String set to either "SUCCESS" or the contents of the exception in case of an error
     */
    public static String execS3APIs() {
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
            log.error("Cannot read S3 bucket list, because: " + e.getMessage());
            return e.getMessage();
        }
    }
}
