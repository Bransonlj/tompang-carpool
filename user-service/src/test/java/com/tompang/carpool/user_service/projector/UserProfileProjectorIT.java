package com.tompang.carpool.user_service.projector;

import com.tompang.carpool.event.driver.DriverRegistrationApprovedEvent;
import com.tompang.carpool.user_service.common.ContainerizedIntegrationTest;
import com.tompang.carpool.user_service.common.KafkaTopics;
import com.tompang.carpool.user_service.model.User;
import com.tompang.carpool.user_service.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserProfileProjectorIT extends ContainerizedIntegrationTest {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void cleanup() {
        userRepository.deleteAll();
    }

    @Test
    void handleDriverRegistered_updatesUserProfileSuccessfully() {
        User createdUser = userRepository.save(
                User.builder()
                        .email("test@email.com")
                        .password("shouldhavebeenencrypted")
                        .firstName("Bob")
                        .lastName("Tan")
                        .build()
        );

        DriverRegistrationApprovedEvent approvedEvent = DriverRegistrationApprovedEvent.newBuilder()
                .setUserId(createdUser.getId())
                .setDriverRegistrationId("driver-reg-123")
                .build();

        // act
        kafkaTemplate.send(KafkaTopics.Driver.DRIVER_REGISTRATION_APPROVED, approvedEvent);

        // assert
        await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                        Optional<User> optionalUser = userRepository.findById(createdUser.getId());
                        assertThat(optionalUser).isPresent();
                        assertThat(optionalUser.get().getDriverId()).isEqualTo(approvedEvent.getDriverRegistrationId());
                });
    }
}
