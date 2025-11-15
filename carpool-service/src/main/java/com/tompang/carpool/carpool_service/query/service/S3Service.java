package com.tompang.carpool.carpool_service.query.service;

import java.time.Duration;

import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
public class S3Service {

    private final static String BUCKET_NAME = "tompang-carpool";

    private final S3Presigner presigner;
    public S3Service(S3Presigner presigner) {
        this.presigner = presigner;
    }

    public String getFileUrl(String key) {
        // Generate presigned URL (valid  for default 12 hours)
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(12))
                .getObjectRequest(request -> 
                    request
                        .bucket(BUCKET_NAME)
                        .key(key))
                .build();

        return presigner.presignGetObject(presignRequest).url().toString();
    }
}
