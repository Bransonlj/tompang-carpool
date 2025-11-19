package com.tompang.carpool.carpool_service.query.service;

import java.net.URI;
import java.time.Duration;

import org.springframework.stereotype.Service;

import com.tompang.carpool.carpool_service.config.AwsProperties;

import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
public class S3Service {
    private final S3Presigner presigner;
    private final AwsProperties awsProperties;

    public S3Service(S3Presigner presigner, AwsProperties awsProperties) {
        this.presigner = presigner;
        this.awsProperties = awsProperties;
    }

    public String getFileUrl(String key) {
        // Generate presigned URL (valid  for default 12 hours)
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(12))
                .getObjectRequest(request -> 
                    request
                        .bucket(awsProperties.getBucketName())
                        .key(key))
                .build();

        String url = presigner.presignGetObject(presignRequest).url().toString();
        // if running s3 locally with localstack, replace the presigned url host with localhost to open with browser
        if (awsProperties.isLocal()) {
            // Parse the endpoint and get the hostname
            URI endpointUri = URI.create(awsProperties.getEndpoint());
            String originalHost = endpointUri.getHost(); // e.g., "localstack"
            url = url.replace(originalHost, "localhost");
        }

        return url;
    }
}
