package com.tompang.carpool.carpool_service.command.domain.carpool;

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

import com.tompang.carpool.carpool_service.command.command.carpool.CreateCarpoolCommand;
import com.tompang.carpool.carpool_service.command.command.carpool.MatchCarpoolCommand;
import com.tompang.carpool.carpool_service.command.domain.LatLong;
import com.tompang.carpool.carpool_service.command.domain.RouteValue;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolCreatedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolMatchedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.exception.CarpoolAndRideRequestAlreadyAssignedException;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestMatchedDomainEvent;
import com.tompang.carpool.event.carpool.CarpoolCreatedEvent;
import com.tompang.carpool.event.carpool.CarpoolMatchedEvent;
import com.tompang.carpool.event.ride_request.RideRequestMatchedEvent;

public class CarpoolAggregateTest {
    @Nested
    @DisplayName("Tests for createCarpool()")
    class CreateCarpoolTests {
        @Test
        void shouldCreateAggregateAndRaiseCarpoolCreatedDomainEvent() {
            // arrange
            CreateCarpoolCommand command = CreateCarpoolCommand.builder()
                .driverId("driver-1")
                .seats(4)
                .arrivalTime(LocalDateTime.now())
                .route(new RouteValue(new LatLong(1, 2), new LatLong(3, 4)))
                .build();
            
            // act
            CarpoolAggregate aggregate = CarpoolAggregate.createCarpool(command);
            
            // assert
            assertNotNull(aggregate.getId());
            assertEquals(command.driverId, aggregate.getDriverId());
            assertEquals(command.seats, aggregate.getTotalSeats());
            assertEquals(0, aggregate.getSeatsAssigned());
            assertEquals(command.arrivalTime, aggregate.getArrivalTime());
            assertEquals(command.route, aggregate.getRoute());
            // should have no matches
            assertTrue(aggregate.getConfirmedRideRequestsCopy().isEmpty());
            assertTrue(aggregate.getPendingRideRequestsCopy().isEmpty());

            // should raise CarpoolCreatedDomainEvent
            assertTrue(aggregate.getUncommittedChanges().get(0) instanceof CarpoolCreatedDomainEvent);
            CarpoolCreatedEvent event = CarpoolCreatedEvent.newBuilder()
                    .setCarpoolId(aggregate.getId())
                    .setDriverId(command.driverId)
                    .setAvailableSeats(command.seats)
                    .setArrivalTime(command.arrivalTime)
                    .setRoute(command.route.toSchemaRoute())
                    .build();
            assertEquals(event, aggregate.getUncommittedChanges().get(0).getEvent());
        }
    }

    @Nested
    @DisplayName("tests for matchRequestToCarpool()")
    class MatchRequestToCarpoolTests {

        @Test
        void shouldUpdateAggregateAndRaiseCarpoolMatchedDomainEvent() {
            // assemble
            CarpoolAggregate aggregate = CarpoolAggregateFactory.created();
            MatchCarpoolCommand command = MatchCarpoolCommand.builder()
                    .carpoolId(aggregate.getId())
                    .requestId("request-1")
                    .build();

            // act
            aggregate.matchRequestToCarpool(command);

            // assert
            assertEquals(List.of(command.requestId), aggregate.getPendingRideRequestsCopy());
            // should raise CarpoolMatchedDomainEvent
            assertTrue(aggregate.getUncommittedChanges().get(0) instanceof CarpoolMatchedDomainEvent);
            CarpoolMatchedEvent event = CarpoolMatchedEvent.newBuilder()
                    .setCarpoolId(aggregate.getId())
                    .setDriverId(aggregate.getDriverId())
                    .setRideRequestId(command.requestId)
                    .build();
            assertEquals(event, aggregate.getUncommittedChanges().get(0).getEvent());
        }

        @Test
        void shouldThrowCarpoolAndRideRequestAlreadyAssignedException_whenRequestAlreadyConfirmed() {
            // assemble
            CarpoolAggregate aggregate = CarpoolAggregateFactory.confirmed();
            String requestId = aggregate.getConfirmedRideRequestsCopy().get(0);
            MatchCarpoolCommand command = MatchCarpoolCommand.builder()
                    .carpoolId(aggregate.getId())
                    .requestId(requestId)
                    .build();

            // act + assert
            CarpoolAndRideRequestAlreadyAssignedException ex = assertThrows(CarpoolAndRideRequestAlreadyAssignedException.class, () -> {
                aggregate.matchRequestToCarpool(command);
            });
            assertEquals(new CarpoolAndRideRequestAlreadyAssignedException(requestId, aggregate.getId()).getMessage(), ex.getMessage());
        }
    }

    @Nested
    @DisplayName("tests for acceptRequestToCarpool()")
    class AcceptRequestToCarpoolTests {
        
    }
}
