package com.tompang.carpool.driver_service.service;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tompang.carpool.driver_service.config.AwsProperties;

import lombok.Builder;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
public class S3Service {
    private final S3Client s3Client;
    private final S3Presigner presigner;
    private final AwsProperties awsProperties;

    public final static String DRIVER_FOLDER = "driver-registration";

    public S3Service(AwsProperties awsProperties, S3Client s3Client, S3Presigner presigner) {
        this.awsProperties = awsProperties;
        this.s3Client = s3Client;
        this.presigner = presigner;
    }

    @Builder
    public static class Key {
        public final String dir;
        public final String id;

        public Key(String dir, String id) {
            this.dir = dir;
            this.id = id;
        }

        @Override
        public String toString() {
            return dir + "/" + id;
        }
    }

    public void uploadFile(Key key, byte[] file, String contentType) throws IOException {
        s3Client.putObject(request -> 
            request
                .bucket(awsProperties.getBucketName())
                .key(key.toString())
                .contentType(contentType),
            RequestBody.fromBytes(file));
    }

    public void uploadFile(Key key, MultipartFile file) throws IOException {
        uploadFile(key, file.getBytes(), file.getContentType());
    }

    public void deleteFile(Key key) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(awsProperties.getBucketName())
                .key(key.toString())
                .build());
    }

    public String getFileUrl(Key key) {
        // Generate presigned URL (valid 5 minutes)
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .getObjectRequest(request -> 
                    request
                        .bucket(awsProperties.getBucketName())
                        .key(key.toString()))
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
