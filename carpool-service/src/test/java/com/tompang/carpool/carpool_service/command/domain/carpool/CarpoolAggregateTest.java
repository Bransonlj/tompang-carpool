package com.tompang.carpool.carpool_service.command.domain.carpool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.tompang.carpool.carpool_service.command.command.carpool.AcceptCarpoolRequestCommand;
import com.tompang.carpool.carpool_service.command.command.carpool.CreateCarpoolCommand;
import com.tompang.carpool.carpool_service.command.command.carpool.DeclineCarpoolRequestCommand;
import com.tompang.carpool.carpool_service.command.command.carpool.InvalidateCarpoolRequestCommand;
import com.tompang.carpool.carpool_service.command.command.carpool.MatchCarpoolCommand;
import com.tompang.carpool.carpool_service.command.domain.LatLong;
import com.tompang.carpool.carpool_service.command.domain.RouteValue;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolCreatedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolMatchedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolRequestAcceptedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolRequestDeclinedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolRequestInvalidatedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.exception.CarpoolAndRideRequestAlreadyAssignedException;
import com.tompang.carpool.carpool_service.command.domain.exception.CarpoolAndRideRequestNotMatchedException;
import com.tompang.carpool.carpool_service.command.domain.exception.DomainException;
import com.tompang.carpool.event.carpool.CarpoolCreatedEvent;
import com.tompang.carpool.event.carpool.CarpoolMatchedEvent;
import com.tompang.carpool.event.carpool.CarpoolRequestAcceptedEvent;
import com.tompang.carpool.event.carpool.CarpoolRequestDeclinedEvent;
import com.tompang.carpool.event.carpool.CarpoolRequestInvalidatedEvent;

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
                .arrivalTime(Instant.now())
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

        @Test
        void shouldUpdateAggregateAndRaiseCarpoolRequestAcceptedDomainEvent() {
            // assemble
            CarpoolAggregate aggregate = CarpoolAggregateFactory.pending();
            String acceptedReqeust = aggregate.getPendingRideRequestsCopy().get(0);
            AcceptCarpoolRequestCommand command = AcceptCarpoolRequestCommand.builder()
                    .carpoolId(aggregate.getId())
                    .requestId(acceptedReqeust)
                    .build();

            // act
            aggregate.acceptRequestToCarpool(command, 1);

            // assert
            assertTrue(!aggregate.getPendingRideRequestsCopy().contains(acceptedReqeust));
            assertTrue(aggregate.getConfirmedRideRequestsCopy().contains(acceptedReqeust));
            assertEquals(1, aggregate.getSeatsAssigned());
            // should raise CarpoolRequestAcceptedDomainEvent
            assertTrue(aggregate.getUncommittedChanges().get(0) instanceof CarpoolRequestAcceptedDomainEvent);
            CarpoolRequestAcceptedEvent event = CarpoolRequestAcceptedEvent.newBuilder()
                    .setCarpoolId(aggregate.getId())
                    .setDriverId(aggregate.getDriverId())
                    .setRideRequestId(acceptedReqeust)
                    .setPassengers(1)
                    .build();
            assertEquals(event, aggregate.getUncommittedChanges().get(0).getEvent());
        }

        @Test
        void shouldThrowDomainException_whenNotEnoughSeatsAvailable() {
            // assemble
            CarpoolAggregate aggregate = CarpoolAggregateFactory.pending();
            AcceptCarpoolRequestCommand command = AcceptCarpoolRequestCommand.builder()
                    .carpoolId(aggregate.getId())
                    .requestId(aggregate.getPendingRideRequestsCopy().get(0))
                    .build();

            // act + assert
            DomainException ex = assertThrows(DomainException.class, () -> {
                aggregate.acceptRequestToCarpool(command, aggregate.getAvailableSeats() + 1);
            });
            assertEquals("Not enough seats available", ex.getMessage());
        }

        @Test
        void shouldThrowCarpoolAndRideRequestAlreadyAssignedException_whenRequestAlreadyConfirmed() {
            // assemble
            CarpoolAggregate aggregate = CarpoolAggregateFactory.confirmed();
            String confirmedRequest = aggregate.getConfirmedRideRequestsCopy().get(0);
            AcceptCarpoolRequestCommand command = AcceptCarpoolRequestCommand.builder()
                    .carpoolId(aggregate.getId())
                    .requestId(confirmedRequest)
                    .build();

            // act + assert
            CarpoolAndRideRequestAlreadyAssignedException ex = assertThrows(CarpoolAndRideRequestAlreadyAssignedException.class, () -> {
                aggregate.acceptRequestToCarpool(command, 1);
            });
            assertEquals(
                    new CarpoolAndRideRequestAlreadyAssignedException(confirmedRequest, command.carpoolId).getMessage(), 
                    ex.getMessage());
        }

        @Test
        void shouldThrowCarpoolAndRideRequestNotMatchedException_whenRequestIsNotInCarpoolPending() {
            // assemble
            CarpoolAggregate aggregate = CarpoolAggregateFactory.pending();
            AcceptCarpoolRequestCommand command = AcceptCarpoolRequestCommand.builder()
                    .carpoolId(aggregate.getId())
                    .requestId("not-matched-request-1")
                    .build();

            // act + assert
            CarpoolAndRideRequestNotMatchedException ex = assertThrows(CarpoolAndRideRequestNotMatchedException.class, () -> {
                aggregate.acceptRequestToCarpool(command, 1);
            });
            assertEquals(
                new CarpoolAndRideRequestNotMatchedException(command.requestId, command.carpoolId).getMessage(), 
                ex.getMessage());
        }
    }

    @Nested
    @DisplayName("tests for declineRequestToCarpool()")
    class DeclineRequestToCarpoolTests {
        
        @Test
        void shouldUpdateAggregateAndRaiseCarpoolRequestDeclinedDomainEvent() {
            // assemble
            CarpoolAggregate aggregate = CarpoolAggregateFactory.pending();
            String declinedRequest = aggregate.getPendingRideRequestsCopy().get(0);
            DeclineCarpoolRequestCommand command = DeclineCarpoolRequestCommand.builder()
                    .carpoolId(aggregate.getId())
                    .requestId(declinedRequest)
                    .build();

            // act
            aggregate.declineRequestToCarpool(command);

            // assert
            assertTrue(!aggregate.getPendingRideRequestsCopy().contains(declinedRequest));
            assertTrue(!aggregate.getConfirmedRideRequestsCopy().contains(declinedRequest)); // verify not confirmed
            // should raise CarpoolRequestDeclinedDomainEvent
            assertTrue(aggregate.getUncommittedChanges().get(0) instanceof CarpoolRequestDeclinedDomainEvent);
            CarpoolRequestDeclinedEvent event = CarpoolRequestDeclinedEvent.newBuilder()
                    .setCarpoolId(aggregate.getId())
                    .setDriverId(aggregate.getDriverId())
                    .setRideRequestId(declinedRequest)
                    .build();
            assertEquals(event, aggregate.getUncommittedChanges().get(0).getEvent());
        }

        @Test
        void shouldThrowCarpoolAndRideRequestAlreadyAssignedException_whenRequestAlreadyConfirmed() {
            // assemble
            CarpoolAggregate aggregate = CarpoolAggregateFactory.confirmed();
            String confirmedRequest = aggregate.getConfirmedRideRequestsCopy().get(0);
            DeclineCarpoolRequestCommand command = DeclineCarpoolRequestCommand.builder()
                    .carpoolId(aggregate.getId())
                    .requestId(confirmedRequest)
                    .build();

            // act + assert
            CarpoolAndRideRequestAlreadyAssignedException ex = assertThrows(CarpoolAndRideRequestAlreadyAssignedException.class, () -> {
                aggregate.declineRequestToCarpool(command);
            });
            assertEquals(
                    new CarpoolAndRideRequestAlreadyAssignedException(confirmedRequest, command.carpoolId).getMessage(), 
                    ex.getMessage());
        }

        @Test
        void shouldThrowCarpoolAndRideRequestNotMatchedException_whenRequestIsNotInCarpoolPending() {
            // assemble
            CarpoolAggregate aggregate = CarpoolAggregateFactory.pending();
            DeclineCarpoolRequestCommand command = DeclineCarpoolRequestCommand.builder()
                    .carpoolId(aggregate.getId())
                    .requestId("not-matched-request-1")
                    .build();

            // act + assert
            CarpoolAndRideRequestNotMatchedException ex = assertThrows(CarpoolAndRideRequestNotMatchedException.class, () -> {
                aggregate.declineRequestToCarpool(command);
            });
            assertEquals(
                new CarpoolAndRideRequestNotMatchedException(command.requestId, command.carpoolId).getMessage(), 
                ex.getMessage());
        }
    }

    @Nested
    @DisplayName("tests for invalidateRequestToCarpool()")
    class InvalidateRequestToCarpoolTests {
        @Test
        void shouldUpdateAggregateAndRaiseCarpoolRequestInvalidatedDomainEvent() {
            // assemble
            CarpoolAggregate aggregate = CarpoolAggregateFactory.pending();
            String invalidatedRequest = aggregate.getPendingRideRequestsCopy().get(0);
            InvalidateCarpoolRequestCommand command = InvalidateCarpoolRequestCommand.builder()
                    .carpoolId(aggregate.getId())
                    .requestId(invalidatedRequest)
                    .reason("test-reason")
                    .build();

            // act
            aggregate.invalidateRequestToCarpool(command);

            // assert
            assertTrue(!aggregate.getPendingRideRequestsCopy().contains(invalidatedRequest));
            assertTrue(!aggregate.getConfirmedRideRequestsCopy().contains(invalidatedRequest)); // verify not confirmed
            // should raise CarpoolRequestInvalidatedDomainEvent

            assertTrue(aggregate.getUncommittedChanges().get(0) instanceof CarpoolRequestInvalidatedDomainEvent);
            CarpoolRequestInvalidatedEvent event = CarpoolRequestInvalidatedEvent.newBuilder()
                    .setCarpoolId(aggregate.getId())
                    .setDriverId(aggregate.getDriverId())
                    .setReason(command.reason)
                    .setRideRequestId(invalidatedRequest)
                    .build();
            assertEquals(event, aggregate.getUncommittedChanges().get(0).getEvent());
        }

        @Test
        void shouldThrowCarpoolAndRideRequestAlreadyAssignedException_whenRequestAlreadyConfirmed() {
            // assemble
            CarpoolAggregate aggregate = CarpoolAggregateFactory.confirmed();
            String confirmedRequest = aggregate.getConfirmedRideRequestsCopy().get(0);
            InvalidateCarpoolRequestCommand command = InvalidateCarpoolRequestCommand.builder()
                    .carpoolId(aggregate.getId())
                    .requestId(confirmedRequest)
                    .reason("test-reason")
                    .build();

            // act + assert
            CarpoolAndRideRequestAlreadyAssignedException ex = assertThrows(CarpoolAndRideRequestAlreadyAssignedException.class, () -> {
                aggregate.invalidateRequestToCarpool(command);
            });
            assertEquals(
                    new CarpoolAndRideRequestAlreadyAssignedException(confirmedRequest, command.carpoolId).getMessage(), 
                    ex.getMessage());
        }

        @Test
        void shouldThrowCarpoolAndRideRequestNotMatchedException_whenRequestIsNotInCarpoolPending() {
            // assemble
            CarpoolAggregate aggregate = CarpoolAggregateFactory.pending();
            InvalidateCarpoolRequestCommand command = InvalidateCarpoolRequestCommand.builder()
                    .carpoolId(aggregate.getId())
                    .requestId("not-matched-request-1")
                    .reason("test-reason")
                    .build();

            // act + assert
            CarpoolAndRideRequestNotMatchedException ex = assertThrows(CarpoolAndRideRequestNotMatchedException.class, () -> {
                aggregate.invalidateRequestToCarpool(command);
            });
            assertEquals(
                new CarpoolAndRideRequestNotMatchedException(command.requestId, command.carpoolId).getMessage(), 
                ex.getMessage());
        }
    }
}
