package com.tompang.carpool.user_service.service;

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

    public S3Service(S3Client s3Client, S3Presigner presigner) {
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

    public void uploadFile(String id, Directory directory, MultipartFile file) throws IOException {
        s3Client.putObject(request -> 
            request
                .bucket(BUCKET_NAME)
                .key(directory.getPath(id))
                .contentType(file.getContentType()),
            RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
    }

    public void deleteFile(String id, Directory directory) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(directory.getPath(id))
                .build());
    }

    public String getFileUrl(String id, Directory directory) {
        // Generate presigned URL (valid  for default 12 hours)
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(12))
                .getObjectRequest(request -> 
                    request
                        .bucket(BUCKET_NAME)
                        .key(directory.getPath(id)))
                .build();

        return presigner.presignGetObject(presignRequest).url().toString();
    }
}
