package com.tompang.carpool.carpool_service.command.service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.await;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;

import com.tompang.carpool.carpool_service.command.command.carpool.CreateCarpoolCommand;
import com.tompang.carpool.carpool_service.command.command.carpool.MatchCarpoolCommand;
import com.tompang.carpool.carpool_service.command.domain.LatLong;
import com.tompang.carpool.carpool_service.command.domain.RouteValue;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.exception.CarpoolAndRideRequestAlreadyMatchedException;
import com.tompang.carpool.carpool_service.command.repository.EventRepository;
import com.tompang.carpool.carpool_service.common.ContainerizedIntegrationTest;
import com.tompang.carpool.carpool_service.common.DomainTopics;
import com.tompang.carpool.carpool_service.common.KafkaTestConsumerFactory;
import com.tompang.carpool.carpool_service.common.kurrent.StreamId;
import com.tompang.carpool.event.carpool.CarpoolCreatedEvent;
import com.tompang.carpool.event.carpool.CarpoolMatchedEvent;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CarpoolCommandHandlerIT extends ContainerizedIntegrationTest {

    @Autowired
    private CarpoolCommandHandler commandHandler;

    @Autowired
    private EventRepository eventRepository;

    private Consumer<String, Object> consumer;
    private CarpoolTestFixture carpoolTestFixture;

    @BeforeEach
    void setup() {
        consumer = KafkaTestConsumerFactory.create(
            kafka.getBootstrapServers(), 
            StringDeserializer.class, 
            KafkaAvroDeserializer.class);
        carpoolTestFixture = new CarpoolTestFixture(commandHandler);
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    void handleCreateCarpool_appendsAndPublishesEvent() {
        CreateCarpoolCommand command = CreateCarpoolCommand.builder()
                .driverId("driver-1")
                .seats(4)
                .arrivalTime(Instant.now().truncatedTo(ChronoUnit.MILLIS))
                .route(new RouteValue(new LatLong(1, 2), new LatLong(3, 4)))
                .build();
                
        consumer.subscribe(List.of(DomainTopics.Carpool.CARPOOL_CREATED));
        consumer.poll(Duration.ofMillis(100)); // triggers assignment, required when using auto.offset.reset=latest

        // act
        String carpoolId = commandHandler.handleCreateCarpool(command);
        StreamId streamId = StreamId.from(EventRepository.CarpoolConstants.STREAM_PREFIX, carpoolId);

        CarpoolCreatedEvent carpoolCreatedEvent = CarpoolCreatedEvent.newBuilder()
                .setCarpoolId(carpoolId)
                .setDriverId(command.driverId)
                .setAvailableSeats(command.seats)
                .setArrivalTime(command.arrivalTime)
                .setRoute(command.route.toSchemaRoute())
                .build();

        // assert kafka event published
        await().pollInterval(Duration.ofSeconds(3))
            .atMost(15, TimeUnit.SECONDS).untilAsserted(() -> {
                ConsumerRecords<String, Object> records = KafkaTestUtils.getRecords(consumer);

                assertThat(records.count()).isGreaterThan(0);

                ConsumerRecord<String, Object> record =
                    records.iterator().next();

                assertThat(record.key()).isEqualTo(streamId.toString());
                assertThat(record.value()).isEqualTo(carpoolCreatedEvent);
            });

        // event appended to eventstore
        List<CarpoolDomainEvent> events = eventRepository.deserializeEvents(streamId);
        assertThat(events.size()).isEqualTo(1);
        assertThat(events.get(0).getEvent()).isEqualTo(carpoolCreatedEvent);
    }

    @Test
    void handleMatchCarpool_appendsAndPublishesEventWhenSuccessful() {
        String createdCarpoolId = carpoolTestFixture.createCarpool();
        StreamId streamId = StreamId.from(EventRepository.CarpoolConstants.STREAM_PREFIX, createdCarpoolId);
        MatchCarpoolCommand command = MatchCarpoolCommand.builder()
                .carpoolId(createdCarpoolId)
                .requestId("request-123")
                .build();
        CarpoolMatchedEvent expectedMatchedEvent = CarpoolMatchedEvent.newBuilder()
                .setDriverId(CarpoolTestFixture.DEFAULT_DRIVER_ID)
                .setCarpoolId(createdCarpoolId)
                .setRideRequestId(command.requestId)
                .build();
        consumer.subscribe(List.of(DomainTopics.Carpool.CARPOOL_MATCHED));
        consumer.poll(Duration.ofMillis(100)); // triggers assignment

        // act
        commandHandler.handleMatchCarpool(command);

        // assert kafka event published
        await().pollInterval(Duration.ofSeconds(3))
            .atMost(15, TimeUnit.SECONDS).untilAsserted(() -> {
                ConsumerRecords<String, Object> records = KafkaTestUtils.getRecords(consumer);

                assertThat(records.isEmpty()).isFalse();
                ConsumerRecord<String, Object> record =
                    records.iterator().next();

                assertThat(record.key()).isEqualTo(streamId.toString());
                assertThat(record.value()).isEqualTo(expectedMatchedEvent);
            });

        // event appended to eventstore
        List<CarpoolDomainEvent> events = eventRepository.deserializeEvents(streamId);
        assertThat(events.size()).isEqualTo(2); // createdEvent, matchedEvent
        assertThat(events.get(1).getEvent()).isEqualTo(expectedMatchedEvent);
    }

    @Test
    void handleMatchCarpool_throwsDomainErrorWhenAlreadyMatchedToRequest() {
        String createdCarpoolId = carpoolTestFixture.createCarpoolWithMatch("request-123");
        MatchCarpoolCommand command = MatchCarpoolCommand.builder()
                .carpoolId(createdCarpoolId)
                .requestId("request-123")
                .build();
        
        assertThatThrownBy(() -> commandHandler.handleMatchCarpool(command))
            .isInstanceOf(CarpoolAndRideRequestAlreadyMatchedException.class)
            .hasMessage(new CarpoolAndRideRequestAlreadyMatchedException(command.requestId, command.carpoolId).getMessage());
    }

}
