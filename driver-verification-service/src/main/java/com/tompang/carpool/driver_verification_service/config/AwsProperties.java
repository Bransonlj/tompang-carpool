package com.tompang.carpool.driver_verification_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "aws")
@Getter
@Setter
public class AwsProperties {

    private String accessKey;
    private String secretKey;
    private String endpoint;
    private String bucketName;
    private boolean local;

    @PostConstruct
    public void logProperties() {
        log.info("AWS Local Mode: {}", local);
        log.info("AWS Endpoint  : {}", endpoint);
        log.info("AWS Bucket    : {}", bucketName);
    }
}