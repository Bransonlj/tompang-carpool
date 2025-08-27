package com.tompang.carpool.driver_service.service;

import java.io.IOException;
import java.time.Duration;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    public final static String FOLDER_NAME = "driver-registration";

    public S3Service(S3Client s3Client, S3Presigner presigner) {
        this.s3Client = s3Client;
        this.presigner = presigner;
    }

    private String getKey(String id) {
        return FOLDER_NAME + "/" + id;
    }

    public void uploadFile(String id, MultipartFile file) throws IOException {
        s3Client.putObject(request -> 
            request
                .bucket(BUCKET_NAME)
                .key(getKey(id))
                .contentType(file.getContentType()),
            RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
    }

    public void deleteFile(String id) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(getKey(id))
                .build());
    }

    public String getFileUrl(String id) {
        // Generate presigned URL (valid 5 minutes)
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .getObjectRequest(request -> 
                    request
                        .bucket(BUCKET_NAME)
                        .key(getKey(id)))
                .build();

        return presigner.presignGetObject(presignRequest).url().toString();
    }
}
