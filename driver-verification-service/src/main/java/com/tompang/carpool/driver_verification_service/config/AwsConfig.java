package com.tompang.carpool.driver_verification_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.rekognition.RekognitionClient;

@Configuration
public class AwsConfig {
    private static String accessKey = "AKIA5ORX65WT3X6EWWFI";
    private static String secretKey = "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";
    public final static String BUCKET_NAME = "tompang-carpool";

    @Bean
    public RekognitionClient s3Client() {
        AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        return RekognitionClient.builder()
                .region(software.amazon.awssdk.regions.Region.AP_SOUTHEAST_2)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
}
