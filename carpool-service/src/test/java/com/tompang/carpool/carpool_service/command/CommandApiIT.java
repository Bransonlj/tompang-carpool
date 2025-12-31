package com.tompang.carpool.carpool_service.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import com.tompang.carpool.carpool_service.command.command.carpool.CreateCarpoolCommand;
import com.tompang.carpool.carpool_service.command.domain.LatLong;
import com.tompang.carpool.carpool_service.command.domain.RouteValue;
import com.tompang.carpool.carpool_service.common.ContainerizedIntegrationTest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CommandApiIT extends ContainerizedIntegrationTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }

	@Test
	void contextLoads() {
	}

    @Test
    void shouldCreateTask() throws Exception {
        CreateCarpoolCommand command = CreateCarpoolCommand.builder()
            .driverId("driver-1")
            .seats(4)
            .arrivalTime(Instant.now().truncatedTo(ChronoUnit.MILLIS))
            .route(new RouteValue(new LatLong(1, 2), new LatLong(3, 4)))
            .build();
            

        given()
            .contentType(ContentType.JSON)
            .body(command)
        .when()
            .post("/api/carpool/command/create")
        .then()
            .statusCode(HttpStatus.CREATED.value());
    } 
}
