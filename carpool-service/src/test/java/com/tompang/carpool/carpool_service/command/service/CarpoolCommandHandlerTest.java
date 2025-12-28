package com.tompang.carpool.carpool_service.command.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import com.tompang.carpool.carpool_service.command.command.carpool.AcceptCarpoolRequestCommand;
import com.tompang.carpool.carpool_service.command.command.carpool.CreateCarpoolCommand;
import com.tompang.carpool.carpool_service.command.command.carpool.DeclineCarpoolRequestCommand;
import com.tompang.carpool.carpool_service.command.command.carpool.InvalidateCarpoolRequestCommand;
import com.tompang.carpool.carpool_service.command.command.carpool.MatchCarpoolCommand;
import com.tompang.carpool.carpool_service.command.domain.LatLong;
import com.tompang.carpool.carpool_service.command.domain.RouteValue;
import com.tompang.carpool.carpool_service.command.domain.carpool.CarpoolAggregate;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolCreatedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolMatchedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolRequestAcceptedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolRequestDeclinedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolRequestInvalidatedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.RideRequestAggregate;
import com.tompang.carpool.carpool_service.command.domain.ride_request.RideRequestStatus;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestAcceptedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestDeclineDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestEvent;
import com.tompang.carpool.carpool_service.command.repository.EventRepository;
import com.tompang.carpool.carpool_service.common.exceptions.BadRequestException;
import com.tompang.carpool.carpool_service.common.kurrent.StreamId;
import com.tompang.carpool.event.carpool.CarpoolCreatedEvent;
import com.tompang.carpool.event.carpool.CarpoolMatchedEvent;
import com.tompang.carpool.event.carpool.CarpoolRequestAcceptedEvent;
import com.tompang.carpool.event.carpool.CarpoolRequestDeclinedEvent;
import com.tompang.carpool.event.carpool.CarpoolRequestInvalidatedEvent;
import com.tompang.carpool.event.ride_request.RideRequestAcceptedEvent;
import com.tompang.carpool.event.ride_request.RideRequestDeclinedEvent;

import io.kurrent.dbclient.ReadResult;

public class CarpoolCommandHandlerTest {
    @Mock
    private EventRepository repository;

    @InjectMocks
    private CarpoolCommandHandler commandHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Nested
    @DisplayName("tests for handleCreateCarpool()")
    class HandleCreateCarpoolTests {

        private CreateCarpoolCommand command;
        private CarpoolAggregate stubAggregate;
        private List<CarpoolDomainEvent> fakeEvents;
        
        @BeforeEach
        void setUp() {
            command = CreateCarpoolCommand.builder()
                    .driverId("driver-1")
                    .seats(4)
                    .arrivalTime(Instant.now())
                    .route(new RouteValue(new LatLong(1, 2), new LatLong(3, 4)))
                    .build();
                    
            stubAggregate = mock(CarpoolAggregate.class);
                        // fake domain events
            fakeEvents = List.of(new CarpoolCreatedDomainEvent(
                CarpoolCreatedEvent.newBuilder()
                    .setCarpoolId("stub-id")
                    .setDriverId(command.driverId)
                    .setAvailableSeats(command.seats)
                    .setArrivalTime(command.arrivalTime)
                    .setRoute(command.route.toSchemaRoute())
                    .build()
            ));
            when(stubAggregate.getId()).thenReturn("stub-id");
            when(stubAggregate.getUncommittedChanges()).thenReturn(fakeEvents);   
        }

        @Test
        void shouldIssueCommandAndAppendAndPublishEvent() {
            try (MockedStatic<CarpoolAggregate> mockedStatic = mockStatic(CarpoolAggregate.class)) {
                // Arrange: stub static factory
                mockedStatic.when(() -> CarpoolAggregate.createCarpool(command))
                            .thenReturn(stubAggregate);
                // act
                String carpoolId = commandHandler.handleCreateCarpool(command);

                // assert
                assertEquals("stub-id", carpoolId);
                verify(repository).appendEvents(
                        eq(StreamId.from(EventRepository.CarpoolConstants.STREAM_PREFIX, "stub-id")),
                        eq(fakeEvents)
                );
            }
        }
    }

    @Nested
    @DisplayName("tests for handleMatchCarpool()")
    class HandleMatchCarpoolTests {
        private MatchCarpoolCommand command;
        private CarpoolAggregate stubAggregate;
        private ReadResult stubReadResult;
        private List<CarpoolDomainEvent> oldEvents;
        private List<CarpoolDomainEvent> newEvents;
        
        @BeforeEach
        void setUp() {
            command = MatchCarpoolCommand.builder()
                    .carpoolId("carpool-1")
                    .requestId("request-1")
                    .build();
                    
            // fake domain events
            oldEvents = List.of();
            newEvents = List.of(new CarpoolMatchedDomainEvent(
                CarpoolMatchedEvent.newBuilder()
                    .setCarpoolId(command.carpoolId)
                    .setRideRequestId(command.requestId)
                    .setDriverId("driver-1")
                    .build()
            ));
            stubReadResult = mock(ReadResult.class);
            when(stubReadResult.getEvents()).thenReturn(null);
            when(stubReadResult.getLastStreamPosition()).thenReturn(Long.valueOf(1));
            stubAggregate = mock(CarpoolAggregate.class);
            when(stubAggregate.getId()).thenReturn(command.carpoolId);
            when(stubAggregate.getUncommittedChanges()).thenReturn(newEvents);
            when(repository.readEvents(StreamId.from(EventRepository.CarpoolConstants.STREAM_PREFIX, command.carpoolId)))
                    .thenReturn(stubReadResult);
            when(repository.deserializeEvent(any())).thenAnswer(invocation -> oldEvents);
        }

        
        @Test
        void shouldIssueCommandAndAppendAndPublishEvent() {
            try (MockedStatic<CarpoolAggregate> mockedStatic = mockStatic(CarpoolAggregate.class)) {
                // Arrange: stub static factory
                mockedStatic.when(() -> CarpoolAggregate.rehydrate(oldEvents))
                            .thenReturn(stubAggregate);
                // act
                commandHandler.handleMatchCarpool(command);

                // assert
                verify(stubAggregate).matchRequestToCarpool(eq(command));
                verify(repository).appendEvents(
                        eq(StreamId.from(EventRepository.CarpoolConstants.STREAM_PREFIX, command.carpoolId)),
                        eq(newEvents),
                        eq(Long.valueOf(1))
                );
            }
        }
    }

    @Nested
    @DisplayName("tests for handleAcceptCarpoolRequest()")
    class HandleAcceptCarpoolRequestTests {
        private AcceptCarpoolRequestCommand command;
        private CarpoolAggregate stubCarpoolAggregate;
        private RideRequestAggregate stubRideRequestAggregate;
        private List<CarpoolDomainEvent> oldCarpoolEvents;
        private List<CarpoolDomainEvent> newCarpoolEvents;
        private List<RideRequestEvent> oldRideRequestEvents;
        private List<RideRequestEvent> newRideRequestEvents;
        private int passengers = 2;
                
        @BeforeEach
        void setUp() {
            command = AcceptCarpoolRequestCommand.builder()
                    .carpoolId("carpool-1")
                    .requestId("request-1")
                    .build();
            ReadResult stubCarpoolReadResult = mock(ReadResult.class);
            when(stubCarpoolReadResult.getEvents()).thenReturn(null);
            when(stubCarpoolReadResult.getLastStreamPosition()).thenReturn(Long.valueOf(1));
            ReadResult stubRequestReadResult = mock(ReadResult.class);
            when(stubRequestReadResult.getEvents()).thenReturn(null);
            when(stubRequestReadResult.getLastStreamPosition()).thenReturn(Long.valueOf(1));
            
            when(repository.readEvents(StreamId.from(EventRepository.CarpoolConstants.STREAM_PREFIX, command.carpoolId)))
                .thenReturn(stubCarpoolReadResult);
            when(repository.readEvents(StreamId.from(EventRepository.RideRequestConstants.STREAM_PREFIX, command.requestId)))
                .thenReturn(stubRequestReadResult);
            
            when(repository.deserializeEvents(any()))    
                    .thenAnswer(invocation -> oldCarpoolEvents)
                    .thenAnswer(invocation -> oldRideRequestEvents);
            
            newCarpoolEvents = List.of(new CarpoolRequestAcceptedDomainEvent(
                CarpoolRequestAcceptedEvent.newBuilder()
                    .setCarpoolId(command.carpoolId)
                    .setRideRequestId(command.requestId)
                    .setDriverId("driver-1")
                    .setPassengers(passengers)
                    .build()
            ));
            newRideRequestEvents = List.of(new RideRequestAcceptedDomainEvent(
                RideRequestAcceptedEvent.newBuilder()
                    .setCarpoolId(command.carpoolId)
                    .setRequestId(command.requestId)
                    .setRiderId("rider-1")
                    .build()
            ));

            stubCarpoolAggregate = mock(CarpoolAggregate.class);
            when(stubCarpoolAggregate.getId()).thenReturn(command.carpoolId);
            when(stubCarpoolAggregate.getUncommittedChanges()).thenReturn(newCarpoolEvents);
            stubRideRequestAggregate = mock(RideRequestAggregate.class);
            when(stubRideRequestAggregate.getId()).thenReturn(command.requestId);
            when(stubRideRequestAggregate.getPassengers()).thenReturn(passengers);
            when(stubRideRequestAggregate.getUncommittedChanges()).thenReturn(newRideRequestEvents);
        }
            
        @Test
        void shouldIssueCommandAndAppendAndPublishEvent() {
            try (MockedStatic<CarpoolAggregate> mockedCarpoolStatic = mockStatic(CarpoolAggregate.class);
                MockedStatic<RideRequestAggregate> mockedRideRequestStatic = mockStatic(RideRequestAggregate.class)) {
                // Arrange: stub static factory
                mockedCarpoolStatic.when(() -> CarpoolAggregate.rehydrate(oldCarpoolEvents))
                        .thenReturn(stubCarpoolAggregate);
                mockedRideRequestStatic.when(() -> RideRequestAggregate.rehydrate(oldRideRequestEvents))
                        .thenReturn(stubRideRequestAggregate);
                when(stubRideRequestAggregate.canAssign()).thenReturn(true);

                // act
                commandHandler.handleAcceptCarpoolRequest(command);

                // asssert
                verify(stubCarpoolAggregate).acceptRequestToCarpool(
                    eq(command), 
                    eq(passengers));
                verify(stubRideRequestAggregate).acceptCarpoolRequest(eq(command));

                verify(repository).appendEvents(
                        eq(StreamId.from(EventRepository.CarpoolConstants.STREAM_PREFIX, command.carpoolId)),
                        eq(newCarpoolEvents),
                        eq(Long.valueOf(1))
                );
                verify(repository).appendEvents(
                        eq(StreamId.from(EventRepository.RideRequestConstants.STREAM_PREFIX, command.requestId)),
                        eq(newRideRequestEvents),
                        eq(Long.valueOf(1))
                );
            }
        }

        @Test
        void shouldThrowBadRequestException_whenRequestCannotBeAssigned() {
            try (MockedStatic<CarpoolAggregate> mockedCarpoolStatic = mockStatic(CarpoolAggregate.class);
                MockedStatic<RideRequestAggregate> mockedRideRequestStatic = mockStatic(RideRequestAggregate.class)) {
                // Arrange: stub static factory
                mockedCarpoolStatic.when(() -> CarpoolAggregate.rehydrate(oldCarpoolEvents))
                        .thenReturn(stubCarpoolAggregate);
                mockedRideRequestStatic.when(() -> RideRequestAggregate.rehydrate(oldRideRequestEvents))
                        .thenReturn(stubRideRequestAggregate);
                when(stubRideRequestAggregate.getStatus()).thenReturn(RideRequestStatus.ASSIGNED);
                when(stubRideRequestAggregate.canAssign()).thenReturn(false);

                // act + assert
                BadRequestException ex = assertThrows(BadRequestException.class, () -> {
                    commandHandler.handleAcceptCarpoolRequest(command);
                });
                assertEquals(
                        new BadRequestException("Request " + command.requestId + " cannot be assigned a carpool, status: " + RideRequestStatus.ASSIGNED).getMessage(), 
                        ex.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("tests for handleDeclineCarpoolRequest()")
    class HandleDeclineCarpoolRequestTests {
        private DeclineCarpoolRequestCommand command;
        private CarpoolAggregate stubCarpoolAggregate;
        private RideRequestAggregate stubRideRequestAggregate;
        private List<CarpoolDomainEvent> oldCarpoolEvents;
        private List<CarpoolDomainEvent> newCarpoolEvents;
        private List<RideRequestEvent> oldRideRequestEvents;
        private List<RideRequestEvent> newRideRequestEvents;
                
        @BeforeEach
        void setUp() {
            command = DeclineCarpoolRequestCommand.builder()
                    .carpoolId("carpool-1")
                    .requestId("request-1")
                    .build();
            ReadResult stubCarpoolReadResult = mock(ReadResult.class);
            when(stubCarpoolReadResult.getEvents()).thenReturn(null);
            when(stubCarpoolReadResult.getLastStreamPosition()).thenReturn(Long.valueOf(1));
            ReadResult stubRequestReadResult = mock(ReadResult.class);
            when(stubRequestReadResult.getEvents()).thenReturn(null);
            when(stubRequestReadResult.getLastStreamPosition()).thenReturn(Long.valueOf(1));
            
            when(repository.readEvents(StreamId.from(EventRepository.CarpoolConstants.STREAM_PREFIX, command.carpoolId)))
                .thenReturn(stubCarpoolReadResult);
            when(repository.readEvents(StreamId.from(EventRepository.RideRequestConstants.STREAM_PREFIX, command.requestId)))
                .thenReturn(stubRequestReadResult);
            
            when(repository.deserializeEvents(any()))    
                    .thenAnswer(invocation -> oldCarpoolEvents)
                    .thenAnswer(invocation -> oldRideRequestEvents);
            
            newCarpoolEvents = List.of(new CarpoolRequestDeclinedDomainEvent(
                CarpoolRequestDeclinedEvent.newBuilder()
                    .setCarpoolId(command.carpoolId)
                    .setRideRequestId(command.requestId)
                    .setDriverId("driver-1")
                    .build()
            ));
            newRideRequestEvents = List.of(new RideRequestDeclineDomainEvent(
                RideRequestDeclinedEvent.newBuilder()
                    .setCarpoolId(command.carpoolId)
                    .setRequestId(command.requestId)
                    .setRiderId("rider-1")
                    .build()
            ));

            stubCarpoolAggregate = mock(CarpoolAggregate.class);
            when(stubCarpoolAggregate.getId()).thenReturn(command.carpoolId);
            when(stubCarpoolAggregate.getUncommittedChanges()).thenReturn(newCarpoolEvents);
            stubRideRequestAggregate = mock(RideRequestAggregate.class);
            when(stubRideRequestAggregate.getId()).thenReturn(command.requestId);
            when(stubRideRequestAggregate.getUncommittedChanges()).thenReturn(newRideRequestEvents);
        }
            
        @Test
        void shouldIssueCommandAndAppendAndPublishEvent() {
            try (MockedStatic<CarpoolAggregate> mockedCarpoolStatic = mockStatic(CarpoolAggregate.class);
                MockedStatic<RideRequestAggregate> mockedRideRequestStatic = mockStatic(RideRequestAggregate.class)) {
                // Arrange: stub static factory
                mockedCarpoolStatic.when(() -> CarpoolAggregate.rehydrate(oldCarpoolEvents))
                        .thenReturn(stubCarpoolAggregate);
                mockedRideRequestStatic.when(() -> RideRequestAggregate.rehydrate(oldRideRequestEvents))
                        .thenReturn(stubRideRequestAggregate);

                // act
                commandHandler.handleDeclineCarpoolRequest(command);

                // asssert
                verify(stubCarpoolAggregate).declineRequestToCarpool(eq(command));
                verify(stubRideRequestAggregate).declineCarpoolRequest(eq(command));

                verify(repository).appendEvents(
                        eq(StreamId.from(EventRepository.CarpoolConstants.STREAM_PREFIX, command.carpoolId)),
                        eq(newCarpoolEvents),
                        eq(Long.valueOf(1))
                );
                verify(repository).appendEvents(
                        eq(StreamId.from(EventRepository.RideRequestConstants.STREAM_PREFIX, command.requestId)),
                        eq(newRideRequestEvents),
                        eq(Long.valueOf(1))
                );
            }
        }
    }
    
    @Nested
    @DisplayName("tests for handleInvalidateCarpoolRequest()")
    class HandleInvalidateCarpoolRequestTests {
        private InvalidateCarpoolRequestCommand command;
        private CarpoolAggregate stubAggregate;
        private ReadResult stubReadResult;
        private List<CarpoolDomainEvent> oldEvents;
        private List<CarpoolDomainEvent> newEvents;
        
        @BeforeEach
        void setUp() {
            command = InvalidateCarpoolRequestCommand.builder()
                    .carpoolId("carpool-1")
                    .requestId("request-1")
                    .reason("test reason")
                    .build();
                    
            // fake domain events
            oldEvents = List.of();
            newEvents = List.of(new CarpoolRequestInvalidatedDomainEvent(
                CarpoolRequestInvalidatedEvent.newBuilder()
                    .setCarpoolId(command.carpoolId)
                    .setRideRequestId(command.requestId)
                    .setDriverId("driver-1")
                    .setReason(command.reason)
                    .build()
            ));
            stubReadResult = mock(ReadResult.class);
            when(stubReadResult.getEvents()).thenReturn(null);
            when(stubReadResult.getLastStreamPosition()).thenReturn(Long.valueOf(1));
            stubAggregate = mock(CarpoolAggregate.class);
            when(stubAggregate.getId()).thenReturn(command.carpoolId);
            when(stubAggregate.getUncommittedChanges()).thenReturn(newEvents);
            when(repository.readEvents(StreamId.from(EventRepository.CarpoolConstants.STREAM_PREFIX, command.carpoolId)))
                    .thenReturn(stubReadResult);
            when(repository.deserializeEvent(any())).thenAnswer(invocation -> oldEvents);
        }
        
        @Test
        void shouldIssueCommandAndAppendAndPublishEvent() {
            try (MockedStatic<CarpoolAggregate> mockedStatic = mockStatic(CarpoolAggregate.class)) {
                // Arrange: stub static factory
                mockedStatic.when(() -> CarpoolAggregate.rehydrate(oldEvents))
                            .thenReturn(stubAggregate);
                // act
                commandHandler.handleInvalidateCarpoolRequest(command);

                // assert
                verify(stubAggregate).invalidateRequestToCarpool(eq(command));
                verify(repository).appendEvents(
                        eq(StreamId.from(EventRepository.CarpoolConstants.STREAM_PREFIX, command.carpoolId)),
                        eq(newEvents),
                        eq(Long.valueOf(1))
                );
            }
        }
    }
}
