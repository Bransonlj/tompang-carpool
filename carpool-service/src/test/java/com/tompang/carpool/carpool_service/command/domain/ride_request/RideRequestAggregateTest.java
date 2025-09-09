package com.tompang.carpool.carpool_service.command.domain.ride_request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.tompang.carpool.carpool_service.command.command.carpool.AcceptCarpoolRequestCommand;
import com.tompang.carpool.carpool_service.command.command.carpool.DeclineCarpoolRequestCommand;
import com.tompang.carpool.carpool_service.command.command.ride_request.CreateRideRequestCommand;
import com.tompang.carpool.carpool_service.command.command.ride_request.FailRideRequestCommand;
import com.tompang.carpool.carpool_service.command.command.ride_request.MatchRideRequestCommand;
import com.tompang.carpool.carpool_service.command.domain.LatLong;
import com.tompang.carpool.carpool_service.command.domain.RouteValue;
import com.tompang.carpool.carpool_service.command.domain.exception.CarpoolNotMatchedException;
import com.tompang.carpool.carpool_service.command.domain.exception.DomainException;
import com.tompang.carpool.carpool_service.command.domain.exception.RideRequestAlreadyAssignedException;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestAcceptedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestCreatedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestDeclineDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestFailedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestMatchedDomainEvent;
import com.tompang.carpool.event.ride_request.RideRequestAcceptedEvent;
import com.tompang.carpool.event.ride_request.RideRequestCreatedEvent;
import com.tompang.carpool.event.ride_request.RideRequestDeclinedEvent;
import com.tompang.carpool.event.ride_request.RideRequestFailedEvent;
import com.tompang.carpool.event.ride_request.RideRequestMatchedEvent;

public class RideRequestAggregateTest {

    @Nested
    @DisplayName("Tests for createRideRequest()")
    class CreateRideRequestTests {

        @Test
        void shouldUpdateAggregateAndRaiseRideRequestCreatedDomainEvent() {
            // arrange
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
        void shouldThrowDomainExceptionIfStartTimeAfterEndTime() {
            // arrange
            CreateRideRequestCommand command = CreateRideRequestCommand.builder()
                .riderId("rider-123")
                .passengers(2)
                .startTime(LocalDateTime.now().plusMinutes(1))
                .endTime(LocalDateTime.now())
                .route(new RouteValue(new LatLong(1, 2), new LatLong(3, 4)))
                .build();
            
            // act + assert
            DomainException ex = assertThrows(DomainException.class, () -> {
                RideRequestAggregate.createRideRequest(command);
            });
            assertEquals("invalid timerange: startTime is after endTime", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Tests for matchRideRequest()")
    class MatchRideRequestTests {
        @Test
        void shouldUpdateAggregateAndRaiseRideRequestMatchedDomainEvent() {
            // arrange
            RideRequestAggregate aggregate = RideRequestAggregateFactory.created();
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
        void shouldThrowRideRequestAlreadyAssignedExceptionIfAlreadyAssigned() {
            // arrange
            RideRequestAggregate aggregate = RideRequestAggregateFactory.assigned();

            List<String> carpoolMatches = List.of("carpool-1", "carpool-2", "carpool-3");
            MatchRideRequestCommand command = MatchRideRequestCommand.builder()
                    .requestId(aggregate.getId())
                    .matchedCarpoolIds(carpoolMatches)
                    .build();
            
            // act + assert
            RideRequestAlreadyAssignedException ex = assertThrows(RideRequestAlreadyAssignedException.class, () -> {
                aggregate.matchRideRequest(command);
            });
            assertEquals(new RideRequestAlreadyAssignedException(aggregate.getId()).getMessage(), ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Tests for failRideRequest()")
    class FailRideRequestTests {

        @Test
        void shouldUpdateAggregateAndRaiseRideRequestFailedDomainEvent() {
            // arrange
            String failedReason = "failed test";
            RideRequestAggregate aggregate = RideRequestAggregateFactory.created();
            FailRideRequestCommand command = FailRideRequestCommand.builder()
                    .requestId(aggregate.getId())
                    .reason(failedReason)
                    .build();
            
            // act
            aggregate.failRideRequest(command);

            // assert
            assertEquals(RideRequestStatus.FAILED, aggregate.getStatus());
            assertTrue(aggregate.getUncommittedChanges().get(0) instanceof RideRequestFailedDomainEvent);
            RideRequestFailedEvent expectedEvent = RideRequestFailedEvent.newBuilder()
                    .setRequestId(aggregate.getId())
                    .setReason(failedReason)
                    .setRiderId(aggregate.getRiderId())
                    .build();
            assertEquals(expectedEvent, aggregate.getUncommittedChanges().get(0).getEvent());          
        }

        @Test
        void shouldThrowDomainExceptionIfHasMatchedCarpools() {
            // arrange
            RideRequestAggregate aggregate = RideRequestAggregateFactory.matched();
            FailRideRequestCommand command = FailRideRequestCommand.builder()
                    .requestId(aggregate.getId())
                    .reason("failed test")
                    .build();

            // act + assert
            DomainException ex = assertThrows(DomainException.class, () -> {
                aggregate.failRideRequest(command);
            });
            assertEquals("RideRequest still has pending carpools matched", ex.getMessage());
        }

        @Test
        void shouldThrowRideRequestAlreadyAssignedExceptionIfAlreadyAssigned() {
            // arrange
            RideRequestAggregate aggregate = RideRequestAggregateFactory.assigned();
            FailRideRequestCommand command = FailRideRequestCommand.builder()
                    .requestId(aggregate.getId())
                    .reason("failed test")
                    .build();

            // act + assert
            RideRequestAlreadyAssignedException ex = assertThrows(RideRequestAlreadyAssignedException.class, () -> {
                aggregate.failRideRequest(command);
            });
            assertEquals(new RideRequestAlreadyAssignedException(aggregate.getId()).getMessage(), ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Tests for acceptCarpoolRequest()")
    class AcceptCarpoolRequestTests {
        @Test
        void shouldUpdateAggregateAndRaiseRideRequestAcceptedDomainEvent() {
            // arrange
            RideRequestAggregate aggregate = RideRequestAggregateFactory.matched();
            String acceptedCarpooId = aggregate.getMatchedCarpoolsCopy().get(0);
            AcceptCarpoolRequestCommand command = AcceptCarpoolRequestCommand.builder()
                    .carpoolId(acceptedCarpooId)
                    .requestId(aggregate.getId())
                    .build();
            
            // act 
            aggregate.acceptCarpoolRequest(command);

            // assert
            assertEquals(RideRequestStatus.ASSIGNED, aggregate.getStatus());
            assertTrue(aggregate.getAssignedCarpool().isPresent());
            assertEquals(acceptedCarpooId, aggregate.getAssignedCarpool().get());
            assertTrue(aggregate.getMatchedCarpoolsCopy().isEmpty());
            assertTrue(aggregate.getUncommittedChanges().get(0) instanceof RideRequestAcceptedDomainEvent);
            RideRequestAcceptedEvent expectedEvent = RideRequestAcceptedEvent.newBuilder()
                    .setRequestId(aggregate.getId())
                    .setCarpoolId(acceptedCarpooId)
                    .setRiderId(aggregate.getRiderId())
                    .build();
            assertEquals(expectedEvent, aggregate.getUncommittedChanges().get(0).getEvent());
        }

        @Test
        void shouldThrowCarpoolNotMatchedExceptionIfAcceptedCarpoolNotInMatched() {
            // arrange
            RideRequestAggregate aggregate = RideRequestAggregateFactory.matched();
            String notMatchedCarpoolId = "not-matched-carpool-123";
            AcceptCarpoolRequestCommand command = AcceptCarpoolRequestCommand.builder()
                    .carpoolId(notMatchedCarpoolId)
                    .requestId(aggregate.getId())
                    .build();
            
            // act + assert
            CarpoolNotMatchedException ex = assertThrows(CarpoolNotMatchedException.class, () -> {
                aggregate.acceptCarpoolRequest(command);
            });
            assertEquals(new CarpoolNotMatchedException(aggregate.getId(), notMatchedCarpoolId).getMessage(), ex.getMessage());
        }

        @Test
        void shouldThrowRideRequestAlreadyAssignedExceptionIfAlreadyAssigned() {
            // arrange
            RideRequestAggregate aggregate = RideRequestAggregateFactory.assigned();
            AcceptCarpoolRequestCommand command = AcceptCarpoolRequestCommand.builder()
                    .carpoolId("dummy-carpool")
                    .requestId(aggregate.getId())
                    .build();

            // act + assert
            RideRequestAlreadyAssignedException ex = assertThrows(RideRequestAlreadyAssignedException.class, () -> {
                aggregate.acceptCarpoolRequest(command);
            });
            assertEquals(new RideRequestAlreadyAssignedException(aggregate.getId()).getMessage(), ex.getMessage());
        
        }
    }
    
    @Nested
    @DisplayName("Tests for acceptCarpoolRequest()")
    class DeclineCarpoolRequestTests {
        @Test
        void shouldUpdateAggregateAndRaiseRideRequestDeclineDomainEvent() {
            // arrange
            RideRequestAggregate aggregate = RideRequestAggregateFactory.matched();
            String declinedCarpoolId = aggregate.getMatchedCarpoolsCopy().get(0);
            DeclineCarpoolRequestCommand command = DeclineCarpoolRequestCommand.builder()
                    .carpoolId(declinedCarpoolId)
                    .requestId(aggregate.getId())
                    .build();
            
            // act
            aggregate.declineCarpoolRequest(command);

            // assert
            assertFalse(aggregate.getMatchedCarpoolsCopy().contains(declinedCarpoolId));
            assertTrue(aggregate.getUncommittedChanges().get(0) instanceof RideRequestDeclineDomainEvent);
            RideRequestDeclinedEvent expectedEvent = RideRequestDeclinedEvent.newBuilder()
                    .setRequestId(aggregate.getId())
                    .setCarpoolId(declinedCarpoolId)
                    .setRiderId(aggregate.getRiderId())
                    .build();
            assertEquals(expectedEvent, aggregate.getUncommittedChanges().get(0).getEvent());
        }

        @Test
        void shouldThrowRideRequestAlreadyAssignedExceptionIfAlreadyAssigned() {
            // arrange
            RideRequestAggregate aggregate = RideRequestAggregateFactory.assigned();
            DeclineCarpoolRequestCommand command = DeclineCarpoolRequestCommand.builder()
                    .carpoolId("dummy-carpool")
                    .requestId(aggregate.getId())
                    .build();

            // act + assert
            RideRequestAlreadyAssignedException ex = assertThrows(RideRequestAlreadyAssignedException.class, () -> {
                aggregate.declineCarpoolRequest(command);
            });
            assertEquals(new RideRequestAlreadyAssignedException(aggregate.getId()).getMessage(), ex.getMessage());
        }

        @Test
        void shouldThrowCarpoolNotMatchedExceptionIfAcceptedCarpoolNotInMatched() {
            // arrange
            RideRequestAggregate aggregate = RideRequestAggregateFactory.matched();
            String notMatchedCarpoolId = "not-matched-carpool-123";
            DeclineCarpoolRequestCommand command = DeclineCarpoolRequestCommand.builder()
                    .carpoolId(notMatchedCarpoolId)
                    .requestId(aggregate.getId())
                    .build();
            
            // act + assert
            CarpoolNotMatchedException ex = assertThrows(CarpoolNotMatchedException.class, () -> {
                aggregate.declineCarpoolRequest(command);
            });
            assertEquals(new CarpoolNotMatchedException(aggregate.getId(), notMatchedCarpoolId).getMessage(), ex.getMessage());
        }
    }
}
