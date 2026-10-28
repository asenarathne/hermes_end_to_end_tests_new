package com.wiley.hermes.helpers;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.io.InputStream;

public class S3Helper {
    
    private static final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

    // Uploads a file from src/test/resources to S3
    public static void uploadResource(String bucket, String key, String resourcePath) {
        InputStream is = S3Helper.class.getResourceAsStream(resourcePath);
        if (is == null) throw new RuntimeException("File not found: " + resourcePath);
        s3Client.putObject(bucket, key, is, null);
        System.out.println("Uploaded to S3: s3://" + bucket + "/" + key);
    }

    // Deletes an object (used for cleanup before test)
    public static void deleteObject(String bucket, String key) {
        if (s3Client.doesObjectExist(bucket, key)) {
            s3Client.deleteObject(bucket, key);
        }
    }

    // Checks if a file exists in the destination folder
    public static boolean waitForFile(String bucket, String prefix, String fileName, int timeoutSeconds) throws InterruptedException {
        long endTime = System.currentTimeMillis() + (timeoutSeconds * 1000L);
        while (System.currentTimeMillis() < endTime) {
            ObjectListing listing = s3Client.listObjects(bucket, prefix);
            for (S3ObjectSummary summary : listing.getObjectSummaries()) {
                if (summary.getKey().endsWith(fileName)) return true;
            }
            Thread.sleep(2000); // poll every 2 seconds
        }
        return false;
    }
}