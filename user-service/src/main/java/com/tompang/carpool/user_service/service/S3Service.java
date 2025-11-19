package com.tompang.carpool.user_service.service;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tompang.carpool.user_service.config.AwsProperties;

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

    public S3Service(AwsProperties awsProperties, S3Client s3Client, S3Presigner presigner) {
        this.awsProperties = awsProperties;
        this.s3Client = s3Client;
        this.presigner = presigner;
    }

    public enum Directory {
        PROFILE_PICTURE("profile-picture");

        private final String directoryName;

        Directory(String directoryName) {
            this.directoryName = directoryName;
        }

        @Override
        public String toString() {
            return directoryName;
        }

        public String getPath(String filename) {
            return directoryName + "/" + filename;
        }
    }

    public void uploadFile(String id, Directory directory, byte[] bytes, String contentType) {
        s3Client.putObject(request -> 
            request
                .bucket(awsProperties.getBucketName())
                .key(directory.getPath(id))
                .contentType(contentType),
            RequestBody.fromBytes(bytes));
    }

    public void uploadFile(String id, Directory directory, MultipartFile file) throws IOException {
        uploadFile(id, directory, file.getBytes(), file.getContentType());
    }

    public void deleteFile(String id, Directory directory) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(awsProperties.getBucketName())
                .key(directory.getPath(id))
                .build());
    }

    public String getFileUrl(String id, Directory directory) {
        // Generate presigned URL (valid  for default 12 hours)
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(12))
                .getObjectRequest(request -> 
                    request
                        .bucket(awsProperties.getBucketName())
                        .key(directory.getPath(id)))
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
