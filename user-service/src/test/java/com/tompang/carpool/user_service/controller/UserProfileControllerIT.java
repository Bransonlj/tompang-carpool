package com.tompang.carpool.user_service.controller;

import com.tompang.carpool.user_service.common.ContainerizedIntegrationTest;
import com.tompang.carpool.user_service.dto.UserProfileDto;
import com.tompang.carpool.user_service.model.User;
import com.tompang.carpool.user_service.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserProfileControllerIT extends ContainerizedIntegrationTest {
    @LocalServerPort
    int port;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void shouldGetUserProfile() {
        User savedUser = userRepository.save(User.builder()
                .email("user@test.com")
                .password("encoded-password")
                .firstName("Bob")
                .lastName("Tan")
                .build()
        );

        UserProfileDto expectedDto = UserProfileDto.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .fullName(savedUser.getFirstName() + " " + savedUser.getLastName())
                .build();


        RestAssured.given()
                        .accept(ContentType.JSON)
                .when()
                        .get("/api/user/profile/" + savedUser.getId())
                .then()
                        .statusCode(HttpStatus.OK.value())
                        .contentType(ContentType.JSON)
                        .body("id", equalTo(expectedDto.id()))
                        .body("fullName", equalTo(expectedDto.fullName()))
                        .body("email", equalTo(expectedDto.email()));

    }
}
