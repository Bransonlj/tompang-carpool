package com.tompang.carpool.geospatial_service.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@Service
public class S3Service {
    private final S3Client s3Client;
    private final static String BUCKET_NAME = "tompang-carpool";

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public enum Directory {
        CARPOOL_ORIGIN_MAP("static-map/carpool/origin"),
        CARPOOL_DESTINATION_MAP("static-map/carpool/destination"),
        RIDE_REQUEST_ORIGIN_MAP("static-map/ride-request/origin"),
        RIDE_REQUEST_DESTINATION_MAP("static-map/ride-request/destination");

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

    public String uploadFile(String id, Directory directory, byte[] file) throws IOException {
        String key = directory.getPath(id);
        s3Client.putObject(request -> 
            request
                .bucket(BUCKET_NAME)
                .key(key)
                .contentType("image/png"),
            RequestBody.fromBytes(file));
        return key;
    }

    public String uploadFile(String id, Directory directory, MultipartFile file) throws IOException {
        String key = directory.getPath(id);
        s3Client.putObject(request -> 
            request
                .bucket(BUCKET_NAME)
                .key(key)
                .contentType(file.getContentType()),
            RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return key;
    }

    public void deleteFile(String id, Directory directory) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(directory.getPath(id))
                .build());
    }
}
