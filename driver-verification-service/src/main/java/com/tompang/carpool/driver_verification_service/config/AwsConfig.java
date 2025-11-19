package com.tompang.carpool.driver_verification_service.config;

import java.net.URI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.rekognition.RekognitionClient;

@Configuration
public class AwsConfig {

    private final AwsProperties awsProperties;

    public AwsConfig(AwsProperties awsProperties) {
        this.awsProperties = awsProperties;
    }

    @Bean
    public RekognitionClient rekognitionClient() {
        AwsCredentials credentials = AwsBasicCredentials.create(awsProperties.getAccessKey(), awsProperties.getSecretKey());
        return RekognitionClient.builder()
                .region(software.amazon.awssdk.regions.Region.AP_SOUTHEAST_2)
                .endpointOverride(URI.create(awsProperties.getEndpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

}
