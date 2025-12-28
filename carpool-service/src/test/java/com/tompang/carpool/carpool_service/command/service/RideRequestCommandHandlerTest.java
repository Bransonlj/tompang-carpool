package com.tompang.carpool.carpool_service.command.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import com.tompang.carpool.carpool_service.command.command.ride_request.CreateRideRequestCommand;
import com.tompang.carpool.carpool_service.command.command.ride_request.FailRideRequestCommand;
import com.tompang.carpool.carpool_service.command.command.ride_request.MatchRideRequestCommand;
import com.tompang.carpool.carpool_service.command.domain.LatLong;
import com.tompang.carpool.carpool_service.command.domain.RouteValue;
import com.tompang.carpool.carpool_service.command.domain.ride_request.RideRequestAggregate;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestCreatedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestFailedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestMatchedDomainEvent;
import com.tompang.carpool.carpool_service.command.repository.EventRepository;
import com.tompang.carpool.carpool_service.common.kurrent.StreamId;
import com.tompang.carpool.event.ride_request.RideRequestCreatedEvent;
import com.tompang.carpool.event.ride_request.RideRequestFailedEvent;
import com.tompang.carpool.event.ride_request.RideRequestMatchedEvent;

import io.kurrent.dbclient.ReadResult;

public class RideRequestCommandHandlerTest {

    @Mock
    private EventRepository repository;

    @InjectMocks
    private RideRequestCommandHandler commandHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Nested
    @DisplayName("tests for handleCreateRideRequest()")
    class HandleCreateRideRequestTests {

        private CreateRideRequestCommand command;
        private RideRequestAggregate stubAggregate;
        private List<RideRequestEvent> fakeEvents;

        @BeforeEach
        void setUp() {
            command = CreateRideRequestCommand.builder()
                    .riderId("rider-123")
                    .passengers(2)
                    .startTime(Instant.now())
                    .endTime(Instant.now())
                    .route(new RouteValue(new LatLong(1, 2), new LatLong(3, 4)))
                    .build();

            // fake domain events
            fakeEvents = List.of(new RideRequestCreatedDomainEvent(
                RideRequestCreatedEvent.newBuilder()
                    .setRequestId("stub-id")
                    .setRiderId(command.riderId)
                    .setPassengers(command.passengers)
                    .setStartTime(command.startTime)
                    .setEndTime(command.endTime)
                    .setRoute(command.route.toSchemaRoute())
                    .build()
            ));

            // stub aggregate with fixed ID + fake events
            stubAggregate = mock(RideRequestAggregate.class);
            when(stubAggregate.getId()).thenReturn("stub-id");
            when(stubAggregate.getUncommittedChanges()).thenReturn(fakeEvents);
        }

        @Test
        void shouldIssueCommandAppendIssueCommandAndPublishEvent() {
            try (MockedStatic<RideRequestAggregate> mockedStatic = mockStatic(RideRequestAggregate.class)) {
                // Arrange: stub static factory
                mockedStatic.when(() -> RideRequestAggregate.createRideRequest(command))
                            .thenReturn(stubAggregate);
                // act
                String requestId = commandHandler.handleCreateRideRequest(command);

                // assert
                assertEquals("stub-id", requestId);
                verify(repository).appendEvents(
                        eq(StreamId.from(EventRepository.RideRequestConstants.STREAM_PREFIX, "stub-id")),
                        eq(fakeEvents)
                );
            }
        }
    }

    @Nested
    @DisplayName("tests for handleMatchRideRequest()")
    class HandleMatchRideRequestTests {
        private MatchRideRequestCommand command;
        private ReadResult stubReadResult;
        private RideRequestAggregate stubAggregate;
        private List<RideRequestEvent> oldEvents;
        private List<RideRequestEvent> newEvents;

        @BeforeEach
        void setUp() {
            command = MatchRideRequestCommand.builder()
                    .requestId("request-123")
                    .matchedCarpoolIds(List.of("carpool-1", "carpool-2", "carpool-3"))
                    .build();

            oldEvents = List.of();
            stubReadResult = mock(ReadResult.class);
            // fake domain events
            newEvents = List.of(new RideRequestMatchedDomainEvent(
                RideRequestMatchedEvent.newBuilder()
                    .setRequestId(command.requestId)
                    .setMatchedCarpoolIds(command.matchedCarpoolIds)
                    .setRiderId("stub-rider")
                    .build()
            ));

            // stub repository returned ReadResult & events
            when(repository.readEvents(StreamId.from(EventRepository.RideRequestConstants.STREAM_PREFIX, command.requestId))).thenReturn(stubReadResult);
            when(stubReadResult.getEvents()).thenReturn(null);
            when(stubReadResult.getLastStreamPosition()).thenReturn(Long.valueOf(1));
            when(repository.deserializeEvents(any())).thenAnswer(invocation -> oldEvents);

            // stub aggregate with fixed ID + fake events
            stubAggregate = mock(RideRequestAggregate.class);
            when(stubAggregate.getId()).thenReturn(command.requestId);
            when(stubAggregate.getUncommittedChanges()).thenReturn(newEvents); // generics erased by mockito
        }

        @Test
        void shouldIssueCommandAppendIssueCommandAndPublishEvent() {
            try (MockedStatic<RideRequestAggregate> mockedStatic = mockStatic(RideRequestAggregate.class)) {
                // Arrange: stub static factory
                mockedStatic.when(() -> RideRequestAggregate.rehydrate(oldEvents))
                            .thenReturn(stubAggregate);
                // act
                commandHandler.handleMatchRideRequest(command);

                // assert
                verify(stubAggregate).matchRideRequest(eq(command));
                verify(repository).appendEvents(
                        eq(StreamId.from(EventRepository.RideRequestConstants.STREAM_PREFIX, command.requestId)),
                        eq(newEvents),
                        eq(Long.valueOf(1))
                );
            }
        }
    }

    @Nested
    @DisplayName("tests for handleFailRideRequest()")
    class HandleFailRideRequestTests {

        private FailRideRequestCommand command;
        private ReadResult stubReadResult;
        private RideRequestAggregate stubAggregate;
        private List<RideRequestEvent> oldEvents;
        private List<RideRequestEvent> newEvents;

        @BeforeEach
        void setUp() {
            command = FailRideRequestCommand.builder()
                    .requestId("request-123")
                    .reason("failed reason")
                    .build();

            oldEvents = List.of();
            stubReadResult = mock(ReadResult.class);
            // fake domain events
            newEvents = List.of(new RideRequestFailedDomainEvent(
                RideRequestFailedEvent.newBuilder()
                    .setRequestId(command.requestId)
                    .setReason(command.reason)
                    .setRiderId("stub-rider")
                    .build()
            ));

            // stub repository returned ReadResult & events
            when(repository.readEvents(StreamId.from(EventRepository.RideRequestConstants.STREAM_PREFIX, command.requestId))).thenReturn(stubReadResult);
            when(stubReadResult.getEvents()).thenReturn(null);
            when(stubReadResult.getLastStreamPosition()).thenReturn(Long.valueOf(1));
            when(repository.deserializeEvents(any())).thenAnswer(invocation -> oldEvents); // generics erased by mockito

            // stub aggregate with fixed ID + fake events
            stubAggregate = mock(RideRequestAggregate.class);
            when(stubAggregate.getId()).thenReturn(command.requestId);
            when(stubAggregate.getUncommittedChanges()).thenReturn(newEvents); 
        }

        @Test
        void shouldIssueCommandAppendIssueCommandAndPublishEvent() {
            try (MockedStatic<RideRequestAggregate> mockedStatic = mockStatic(RideRequestAggregate.class)) {
                // Arrange: stub static factory
                mockedStatic.when(() -> RideRequestAggregate.rehydrate(oldEvents))
                            .thenReturn(stubAggregate);
                // act
                commandHandler.handleFailRideRequest(command);

                // assert
                verify(stubAggregate).failRideRequest(eq(command));
                verify(repository).appendEvents(
                        eq(StreamId.from(EventRepository.RideRequestConstants.STREAM_PREFIX, command.requestId)),
                        eq(newEvents),
                        eq(Long.valueOf(1))
                );
            }
        }
    }
}
