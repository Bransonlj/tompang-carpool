package com.tompang.carpool.driver_service.service;

import java.io.IOException;
import java.time.Duration;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    private final static String BUCKET_NAME = "tompang-carpool";

    public final static String DRIVER_FOLDER = "driver-registration";

    public S3Service(S3Client s3Client, S3Presigner presigner) {
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

    public void uploadFile(Key key, MultipartFile file) throws IOException {
        s3Client.putObject(request -> 
            request
                .bucket(BUCKET_NAME)
                .key(key.toString())
                .contentType(file.getContentType()),
            RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
    }

    public void deleteFile(Key key) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(key.toString())
                .build());
    }

    public String getFileUrl(Key key) {
        // Generate presigned URL (valid 5 minutes)
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .getObjectRequest(request -> 
                    request
                        .bucket(BUCKET_NAME)
                        .key(key.toString()))
                .build();

        return presigner.presignGetObject(presignRequest).url().toString();
    }
}
