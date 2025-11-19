package com.tompang.carpool.geospatial_service.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tompang.carpool.geospatial_service.config.AwsProperties;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@Service
public class S3Service {
    private final S3Client s3Client;
    private final AwsProperties awsProperties;

    public S3Service(AwsProperties awsProperties, S3Client s3Client) {
        this.awsProperties = awsProperties;
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

    public String uploadFile(String id, Directory directory, byte[] file, String contentType) throws IOException {
        String key = directory.getPath(id);
        s3Client.putObject(request -> 
            request
                .bucket(awsProperties.getBucketName())
                .key(key)
                .contentType(contentType),
            RequestBody.fromBytes(file));
        return key;
    }

    /**
     * Upload file with default contentType "image/png"
     * @param id
     * @param directory
     * @param file
     * @return
     * @throws IOException
     */
    public String uploadFile(String id, Directory directory, byte[] file) throws IOException {
        return uploadFile(id, directory, file, "image/png");
    }

    public String uploadFile(String id, Directory directory, MultipartFile file) throws IOException {
        return uploadFile(id, directory, file.getBytes(), file.getContentType());
    }

    public void deleteFile(String id, Directory directory) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(awsProperties.getBucketName())
                .key(directory.getPath(id))
                .build());
    }
}
