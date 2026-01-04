package com.tompang.carpool.carpool_service.command.service;

import com.tompang.carpool.carpool_service.command.command.ride_request.CreateRideRequestCommand;
import com.tompang.carpool.carpool_service.command.domain.LatLong;
import com.tompang.carpool.carpool_service.command.domain.RouteValue;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestEvent;
import com.tompang.carpool.carpool_service.command.repository.EventRepository;
import com.tompang.carpool.carpool_service.common.ContainerizedIntegrationTest;
import com.tompang.carpool.carpool_service.common.DomainTopics;
import com.tompang.carpool.carpool_service.common.KafkaTestConsumerFactory;
import com.tompang.carpool.carpool_service.common.kurrent.StreamId;
import com.tompang.carpool.event.ride_request.RideRequestCreatedEvent;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
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

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RideRequestCommandHandlerIT extends ContainerizedIntegrationTest {
    @Autowired
    private RideRequestCommandHandler commandHandler;

    @Autowired
    private EventRepository eventRepository;

    private Consumer<String, Object> consumer;

    @BeforeEach
    void setup() {
        consumer = KafkaTestConsumerFactory.create(
                kafka.getBootstrapServers(),
                StringDeserializer.class,
                KafkaAvroDeserializer.class);
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    void handleCreateRideRequest_appendsAndPublishesEvent() {
        CreateRideRequestCommand command = CreateRideRequestCommand.builder()
                .riderId("rider-123")
                .passengers(2)
                .startTime(Instant.now().truncatedTo(ChronoUnit.MILLIS))
                .endTime(Instant.now().plus(30, ChronoUnit.MINUTES).truncatedTo(ChronoUnit.MILLIS))
                .route(new RouteValue(new LatLong(1, 2), new LatLong(3, 4)))
                .build();

        consumer.subscribe(List.of(DomainTopics.RideRequest.REQUEST_CREATED));
        consumer.poll(Duration.ofMillis(100)); // triggers assignment, required when using auto.offset.reset=latest

        // act
        String requestId = commandHandler.handleCreateRideRequest(command);
        StreamId streamId = StreamId.from(EventRepository.RideRequestConstants.STREAM_PREFIX, requestId);

        RideRequestCreatedEvent rideRequestCreatedEvent = RideRequestCreatedEvent.newBuilder()
                .setRequestId(requestId)
                .setRiderId(command.riderId)
                .setPassengers(command.passengers)
                .setStartTime(command.startTime)
                .setEndTime(command.endTime)
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
                    assertThat(record.value()).isEqualTo(rideRequestCreatedEvent);
                });

        // event appended to eventstore
        List<RideRequestEvent> events = eventRepository.deserializeEvents(streamId);
        assertThat(events.isEmpty()).isFalse();
        assertThat(events.get(0).getEvent()).isEqualTo(rideRequestCreatedEvent);
    }
}
