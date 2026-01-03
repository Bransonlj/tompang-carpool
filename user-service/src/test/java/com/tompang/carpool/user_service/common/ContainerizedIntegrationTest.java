package com.tompang.carpool.user_service.common;

import com.tompang.carpool.user_service.config.AwsProperties;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.localstack.LocalStackContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class ContainerizedIntegrationTest {
    protected static final ConfluentKafkaContainer kafka = new ConfluentKafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.7.7")
    );

    protected static final PostgreSQLContainer postgres = new PostgreSQLContainer(
            DockerImageName.parse("postgres:18.1-alpine")
    );

    protected static final LocalStackContainer localstack = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:4.12")
    );

    static {
        kafka.start();
        postgres.start();
        localstack.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.producer.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.kafka.consumer.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add(AwsProperties.ACCESS_KEY, localstack::getAccessKey);
        registry.add(AwsProperties.SECRET_KEY, localstack::getSecretKey);
        registry.add(AwsProperties.ENDPOINT, localstack::getEndpoint);
        registry.add(AwsProperties.LOCAL, () -> true);
    }
}
