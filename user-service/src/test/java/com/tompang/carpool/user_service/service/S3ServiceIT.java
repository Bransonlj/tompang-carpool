package com.tompang.carpool.user_service.service;

import com.tompang.carpool.user_service.common.ContainerizedIntegrationTest;
import com.tompang.carpool.user_service.config.AwsProperties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class S3ServiceIT extends ContainerizedIntegrationTest {

    static final String BUCKET_NAME = UUID.randomUUID().toString();

    @DynamicPropertySource
    static void addExtraProperties(DynamicPropertyRegistry registry) {
        registry.add(AwsProperties.BUCKET_NAME, () -> BUCKET_NAME);
    }

    @BeforeAll
    static void beforeAll() throws IOException, InterruptedException {
        localstack.execInContainer("awslocal", "s3", "mb", "s3://" + BUCKET_NAME);
    }

    @Autowired
    private S3Service s3Service;

    @Autowired
    private S3Client s3Client;

    @Autowired
    private AwsProperties awsProperties;

    @Test
    void uploadFileSuccessfully() {
        String id = "id-123";
        S3Service.Directory directory = S3Service.Directory.PROFILE_PICTURE;
        byte[] content = "test s3 data".getBytes(StandardCharsets.UTF_8);
        String contentType = "text/plain";

        // act
        s3Service.uploadFile(id, directory, content, contentType);

        // assert
        ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(
                GetObjectRequest.builder()
                        .bucket(awsProperties.getBucketName())
                        .key(directory.getPath(id))
                        .build()
        );

        assertThat(response.response().contentType()).isEqualTo(contentType);
        assertThat(response.asByteArray()).isEqualTo(content);
    }

}