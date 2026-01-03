package com.tompang.carpool.user_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = AwsProperties.PREFIX)
@Getter
@Setter
public class AwsProperties {


    public static final String PREFIX = "aws";

    public static final String ACCESS_KEY  = PREFIX + ".access-key";
    public static final String SECRET_KEY  = PREFIX + ".secret-key";
    public static final String ENDPOINT    = PREFIX + ".endpoint";
    public static final String BUCKET_NAME = PREFIX + ".bucket-name";
    public static final String LOCAL       = PREFIX + ".local";

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