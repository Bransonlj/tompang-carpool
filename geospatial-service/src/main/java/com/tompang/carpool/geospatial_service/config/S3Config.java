package com.tompang.carpool.geospatial_service.config;

import java.net.URI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

@Configuration
public class S3Config {

    private final AwsProperties awsProperties;

    public S3Config(AwsProperties awsProperties) {
        this.awsProperties = awsProperties;
    }

    @Bean
    public S3Client s3Client() {
        AwsCredentials credentials = AwsBasicCredentials.create(awsProperties.getAccessKey(), awsProperties.getSecretKey());
        return S3Client.builder()
                .region(Region.AP_SOUTHEAST_2)
                .endpointOverride(URI.create(awsProperties.getEndpoint()))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(awsProperties.isLocal()).build()) // only enable pathStyleAccess for localstack emulation
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
}
