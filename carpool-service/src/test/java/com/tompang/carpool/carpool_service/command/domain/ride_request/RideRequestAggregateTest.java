package com.tompang.carpool.carpool_service.command.domain.ride_request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.tompang.carpool.carpool_service.command.command.ride_request.CreateRideRequestCommand;
import com.tompang.carpool.carpool_service.command.command.ride_request.MatchRideRequestCommand;
import com.tompang.carpool.carpool_service.command.domain.LatLong;
import com.tompang.carpool.carpool_service.command.domain.RouteValue;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestCreatedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestMatchedDomainEvent;
import com.tompang.carpool.event.ride_request.RideRequestCreatedEvent;
import com.tompang.carpool.event.ride_request.RideRequestMatchedEvent;

public class RideRequestAggregateTest {

    @Nested
    @DisplayName("Tests for createRideRequest()")
    class CreateRideRequestTests {

        @Test
        void shouldUpdateAggregateAndRaiseRideRequestCreatedDomainEvent() {
            // assemble
            CreateRideRequestCommand command = CreateRideRequestCommand.builder()
                .riderId("rider-123")
                .passengers(2)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now())
                .route(new RouteValue(new LatLong(1, 2), new LatLong(3, 4)))
                .build();
            
            // act
            RideRequestAggregate aggregate = RideRequestAggregate.createRideRequest(command);
            
            // assert
            assertNotNull(aggregate.getId());
            assertEquals(command.riderId, aggregate.getRiderId());
            assertEquals(command.passengers, aggregate.getPassengers());
            assertEquals(command.startTime, aggregate.getStartTime());
            assertEquals(command.endTime, aggregate.getEndTime());
            assertEquals(command.route, aggregate.getRoute());
            // should have no matches
            assertTrue(aggregate.getMatchedCarpoolsCopy().isEmpty());
            assertTrue(aggregate.getAssignedCarpool().isEmpty());

            // should raise RideRequestCreatedDomainEvent
            assertTrue(aggregate.getUncommittedChanges().get(0) instanceof RideRequestCreatedDomainEvent);

            RideRequestCreatedEvent event = RideRequestCreatedEvent.newBuilder()
                    .setRequestId(aggregate.getId())
                    .setRiderId(command.riderId)
                    .setPassengers(command.passengers)
                    .setStartTime(command.startTime)
                    .setEndTime(command.endTime)
                    .setRoute(command.route.toSchemaRoute())
                    .build();
            assertEquals(event, aggregate.getUncommittedChanges().get(0).getEvent());
        }

        @Test
        void shouldFailIfStartTimeAfterEndTime() {
            // assemble
            CreateRideRequestCommand command = CreateRideRequestCommand.builder()
                .riderId("rider-123")
                .passengers(2)
                .startTime(LocalDateTime.now().plusMinutes(1))
                .endTime(LocalDateTime.now())
                .route(new RouteValue(new LatLong(1, 2), new LatLong(3, 4)))
                .build();
            
            // act + assert
            RuntimeException ex = assertThrows(RuntimeException.class, () -> {
                RideRequestAggregate.createRideRequest(command);
            });
            assertEquals("invalid timerange: startTime is after endTime", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Tests for matchRideRequest()")
    class MatchRideRequestTests {

        private RideRequestAggregate aggregate;

        @BeforeEach
        void setup() {
            // Initialize aggregate with a created ride request
            CreateRideRequestCommand command = CreateRideRequestCommand.builder()
                .riderId("rider-123")
                .passengers(2)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now())
                .route(new RouteValue(new LatLong(1, 2), new LatLong(3, 4)))
                .build();

            aggregate = RideRequestAggregate.createRideRequest(command);
            // flush changes to reset uncommitted events
            aggregate.clearUncommittedChanges();
        }

        @Test
        void shouldUpdateAggregateAndRaiseRideRequestMatchedDomainEvent() {
            // assemble
            List<String> carpoolMatches = List.of("carpool-1", "carpool-2", "carpool-3");
            MatchRideRequestCommand command = MatchRideRequestCommand.builder()
                    .requestId(aggregate.getId())
                    .matchedCarpoolIds(carpoolMatches)
                    .build();
            
            // act
            aggregate.matchRideRequest(command);

            // assert
            assertEquals(command.matchedCarpoolIds, aggregate.getMatchedCarpoolsCopy());
            // should raise RideRequestMatchedDomainEvent
            assertTrue(aggregate.getUncommittedChanges().get(0) instanceof RideRequestMatchedDomainEvent);

            RideRequestMatchedEvent event = RideRequestMatchedEvent.newBuilder()
                    .setRequestId(aggregate.getId())
                    .setRiderId(aggregate.getRiderId())
                    .setMatchedCarpoolIds(carpoolMatches)
                    .build();
            assertEquals(event, aggregate.getUncommittedChanges().get(0).getEvent());
        }

        @Test
        void shouldFailIfHasAssignedCarpool() {
            // TODO create test factory and get aggregate that is already assigned for this testcase.
        }
    }
}
