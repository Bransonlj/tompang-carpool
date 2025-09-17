package com.tompang.carpool.carpool_service.command.process;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import com.tompang.carpool.carpool_service.command.command.carpool.InvalidateCarpoolRequestCommand;
import com.tompang.carpool.carpool_service.command.command.carpool.MatchCarpoolCommand;
import com.tompang.carpool.carpool_service.command.command.ride_request.FailRideRequestCommand;
import com.tompang.carpool.carpool_service.command.command.ride_request.MatchRideRequestCommand;
import com.tompang.carpool.carpool_service.command.domain.LatLong;
import com.tompang.carpool.carpool_service.command.domain.RouteValue;
import com.tompang.carpool.carpool_service.command.domain.ride_request.RideRequestAggregate;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestEvent;
import com.tompang.carpool.carpool_service.command.repository.EventRepository;
import com.tompang.carpool.carpool_service.command.service.CarpoolCommandHandler;
import com.tompang.carpool.carpool_service.command.service.RideRequestCommandHandler;
import com.tompang.carpool.carpool_service.common.GeoUtils;
import com.tompang.carpool.carpool_service.common.kurrent.StreamId;
import com.tompang.carpool.carpool_service.query.entity.Carpool;
import com.tompang.carpool.carpool_service.query.service.CarpoolQueryService;
import com.tompang.carpool.event.ride_request.RideRequestAcceptedEvent;
import com.tompang.carpool.event.ride_request.RideRequestCreatedEvent;
import com.tompang.carpool.event.ride_request.RideRequestDeclinedEvent;

import io.kurrent.dbclient.ReadResult;

public class RideRequestProcessManagerTest {
    @Mock
    private CarpoolQueryService carpoolQueryService;
    @Mock
    private CarpoolCommandHandler carpoolCommandHandler;
    @Mock
    private RideRequestCommandHandler requestCommandHandler;
    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private RideRequestProcessManager processManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Nested
    @DisplayName("tests for handleRideRequestCreated()")
    class HandleRideRequestCreatedTests {

        private RideRequestCreatedEvent event;
        
        @BeforeEach
        void setUp() {
            event = RideRequestCreatedEvent.newBuilder()
                    .setRequestId("request-1")
                    .setRiderId("rider-1")
                    .setPassengers(2)
                    .setStartTime(LocalDateTime.now())
                    .setEndTime(LocalDateTime.now())
                    .setRoute(new RouteValue(new LatLong(1, 2), new LatLong(3, 4)).toSchemaRoute())
                    .build();
        }

        @Test
        void shouldInvokeMatchedCommandsForCarpoolAndRideRequest_whenSuccess() {
            // arrange
            List<Carpool> matchedCarpoolEntities = List.of(
                Carpool.builder()
                    .id("carpool-1")
                    .build(),
                Carpool.builder()
                    .id("carpool-2")
                    .build());
            when(carpoolQueryService.getCarpoolsByRouteInRangeWithSeats(                
                    GeoUtils.createPoint(event.getRoute().getOrigin()), 
                    GeoUtils.createPoint(event.getRoute().getDestination()), 
                    5000, event.getStartTime(), event.getEndTime(), event.getPassengers())
            ).thenReturn(matchedCarpoolEntities);
            
            // act
            processManager.handleRideRequestCreated(event);

            // assert
            verify(carpoolCommandHandler, times(matchedCarpoolEntities.size()))
                .handleMatchCarpool(any(MatchCarpoolCommand.class));
            verify(carpoolCommandHandler).handleMatchCarpool(
                    eq(new MatchCarpoolCommand(
                            matchedCarpoolEntities.get(0).getId(), 
                            event.getRequestId()))
            );
            verify(carpoolCommandHandler).handleMatchCarpool(
                    eq(new MatchCarpoolCommand(
                            matchedCarpoolEntities.get(1).getId(), 
                            event.getRequestId()))
            );
            verify(requestCommandHandler).handleMatchRideRequest(
                eq(new MatchRideRequestCommand(
                    event.getRequestId(), 
                    matchedCarpoolEntities.stream().map(carpool -> carpool.getId()).toList()))
            );
            
        }

        @Test
        @DisplayName("should invoke FailRideRequestCommand if exception encountered getting matching carpools")
        void shouldInvokeFailRideRequestCommand_whenFailedToGetMatchingCarpools() {
            // arrange
            when(carpoolQueryService.getCarpoolsByRouteInRangeWithSeats(any(), any(), anyDouble(), any(), any(), anyInt())).thenThrow(new RuntimeException());
        
            // act
            processManager.handleRideRequestCreated(event);

            // assert
            verify(requestCommandHandler).handleFailRideRequest(
                eq(new FailRideRequestCommand(event.getRequestId(), "Error finding carpool matches"))
            );
        }

        @Test
        void shouldInvokeFailRideRequestCommand_whenNoMatchingCarpoolsFound() {
            // arrange
            when(carpoolQueryService.getCarpoolsByRouteInRangeWithSeats(                
                    GeoUtils.createPoint(event.getRoute().getOrigin()), 
                    GeoUtils.createPoint(event.getRoute().getDestination()), 
                    5000, event.getStartTime(), event.getEndTime(), event.getPassengers())
            ).thenReturn(List.of());

            // act
            processManager.handleRideRequestCreated(event);

            // assert
            verify(requestCommandHandler).handleFailRideRequest(
                eq(new FailRideRequestCommand(event.getRequestId(), "No carpool matches found"))
            );
        }
    }

    @Nested
    @DisplayName("tests for handleRideRequestAccepted()")
    class HandleRideRequestAcceptedTests {

        @Test
        void shouldInvokeInvalidateCarpoolRequestCommandForLeftoverCarpools() {
                // Arrange
                List<String> matchedCarpools = List.of("carpool-1", "carpool-2", "carpool-3");
                RideRequestAcceptedEvent event = RideRequestAcceptedEvent.newBuilder()
                    .setRequestId("request-1")
                    .setCarpoolId(matchedCarpools.get(0))
                    .setLeftoverCarpoolIds(List.of(matchedCarpools.get(1), matchedCarpools.get(2)))
                    .setRiderId("rider-1")
                    .build();;
                // act
                processManager.handleRideRequestAccepted(event);

                // assert
                verify(carpoolCommandHandler, times(matchedCarpools.size() - 1))
                    .handleInvalidateCarpoolRequest(any(InvalidateCarpoolRequestCommand.class));
                verify(carpoolCommandHandler).handleInvalidateCarpoolRequest(
                        eq(new InvalidateCarpoolRequestCommand(
                                matchedCarpools.get(1), 
                                event.getRequestId(),
                                "RideRequest has been accepted by another Carpool"))
                );
                verify(carpoolCommandHandler).handleInvalidateCarpoolRequest(
                        eq(new InvalidateCarpoolRequestCommand(
                                matchedCarpools.get(2), 
                                event.getRequestId(),
                                "RideRequest has been accepted by another Carpool"))
                );
        }
    }

    @Nested
    @DisplayName("tests for handleRideRequestDeclined()")
    class HandleRideRequestDeclinedTests {
        private RideRequestDeclinedEvent event;
        private List<RideRequestEvent> oldEvents;

        @BeforeEach
        void setUp() {
            event = RideRequestDeclinedEvent.newBuilder()
                    .setCarpoolId("carpool-1")
                    .setRequestId("request-1")
                    .setRiderId("rider-1")
                    .build();
            // stub repository returned ReadResult & events
            ReadResult stubReadResult = mock(ReadResult.class);
            when(eventRepository.readEvents(StreamId.from(EventRepository.RideRequestConstants.STREAM_PREFIX, event.getRequestId())))
                    .thenReturn(stubReadResult);
            when(stubReadResult.getEvents()).thenReturn(null);
            when(eventRepository.deserializeEvents(any())).thenAnswer(invocation -> oldEvents);

            
        }

        @Test
        void shouldInvokeFailRideRequestCommand_whenNoMoreMatchingCarpools() {
            try (MockedStatic<RideRequestAggregate> mockedStatic = mockStatic(RideRequestAggregate.class)) {
                // Arrange: stub static factory
                RideRequestAggregate stubAggregate = mock(RideRequestAggregate.class);
                when(stubAggregate.getId()).thenReturn(event.getRequestId());
                when(stubAggregate.getMatchedCarpoolsCopy()).thenReturn(List.of()); 
                mockedStatic.when(() -> RideRequestAggregate.rehydrate(oldEvents))
                        .thenReturn(stubAggregate);

                // act
                processManager.handleRideRequestDeclined(event);

                // assert  
                verify(requestCommandHandler).handleFailRideRequest(
                        eq(new FailRideRequestCommand(
                                event.getRequestId(),
                                "All matched carpools declined the request"))
                );

            }
        }

        @Test
        void shouldNotInvokeFailRideRequestCommand_whenStillHasMatchingCarpools() {
            try (MockedStatic<RideRequestAggregate> mockedStatic = mockStatic(RideRequestAggregate.class)) {
                // Arrange: stub static factory
                RideRequestAggregate stubAggregate = mock(RideRequestAggregate.class);
                when(stubAggregate.getId()).thenReturn(event.getRequestId());
                when(stubAggregate.getMatchedCarpoolsCopy()).thenReturn(List.of("carpool-2")); 
                mockedStatic.when(() -> RideRequestAggregate.rehydrate(oldEvents))
                        .thenReturn(stubAggregate);

                // act
                processManager.handleRideRequestDeclined(event);

                // assert  
                verify(requestCommandHandler, never()).handleFailRideRequest(any());

            }
        }
    }
}
