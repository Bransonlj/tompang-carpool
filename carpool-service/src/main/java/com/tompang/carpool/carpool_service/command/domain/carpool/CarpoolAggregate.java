package com.tompang.carpool.carpool_service.command.domain.carpool;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.tompang.carpool.carpool_service.command.command.carpool.AcceptCarpoolRequestCommand;
import com.tompang.carpool.carpool_service.command.command.carpool.CreateCarpoolCommand;
import com.tompang.carpool.carpool_service.command.command.carpool.DeclineCarpoolRequestCommand;
import com.tompang.carpool.carpool_service.command.command.carpool.InvalidateCarpoolRequestCommand;
import com.tompang.carpool.carpool_service.command.command.carpool.MatchCarpoolCommand;
import com.tompang.carpool.carpool_service.command.domain.RouteValue;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolCreatedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolMatchedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolRequestAcceptedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolRequestDeclinedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolRequestInvalidatedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.exception.CarpoolAndRideRequestNotMatchedException;
import com.tompang.carpool.carpool_service.command.domain.exception.DomainException;
import com.tompang.carpool.carpool_service.command.domain.exception.CarpoolAndRideRequestAlreadyAssignedException;
import com.tompang.carpool.event.carpool.CarpoolCreatedEvent;
import com.tompang.carpool.event.carpool.CarpoolMatchedEvent;
import com.tompang.carpool.event.carpool.CarpoolRequestAcceptedEvent;
import com.tompang.carpool.event.carpool.CarpoolRequestDeclinedEvent;
import com.tompang.carpool.event.carpool.CarpoolRequestInvalidatedEvent;

public class CarpoolAggregate {

    private String id;
    private int totalSeats;
    private int seatsAssigned = 0;
    private List<String> confirmedRideRequests = new ArrayList<>();;
    private List<String> pendingRideRequests = new ArrayList<>();;
    private String driverId;

    private RouteValue route;
    private Instant arrivalTime;

    // List of new events to be persisted
    private final List<CarpoolDomainEvent> changes = new ArrayList<>();

    public static CarpoolAggregate rehydrate(List<CarpoolDomainEvent> history) {
        CarpoolAggregate carpool = new CarpoolAggregate();
        for (CarpoolDomainEvent event : history) {
            carpool.apply(event);
        }
        return carpool;
    }

    public static CarpoolAggregate createCarpool(CreateCarpoolCommand command) {
        CarpoolAggregate carpool = new CarpoolAggregate();
        CarpoolCreatedDomainEvent domainEvent = new CarpoolCreatedDomainEvent(
            new CarpoolCreatedEvent(
                UUID.randomUUID().toString(), command.seats, command.driverId, 
                command.arrivalTime, 
                command.route.toSchemaRoute()
            )
        );
        carpool.raiseEvent(domainEvent);
        return carpool;
    }

    public void matchRequestToCarpool(MatchCarpoolCommand command) {
        ensureNotAlreadyAssigned(command.requestId);
        CarpoolMatchedDomainEvent domainEvent = new CarpoolMatchedDomainEvent(
                CarpoolMatchedEvent.newBuilder()
                        .setCarpoolId(command.carpoolId)
                        .setRideRequestId(command.requestId)
                        .setDriverId(this.driverId)
                        .build());
        raiseEvent(domainEvent);
    } 

    public void acceptRequestToCarpool(AcceptCarpoolRequestCommand command, int passengers) {
        // validate if got seats
        ensureEnoughSeats(passengers);
        ensureNotAlreadyAssigned(command.requestId);
        ensureMatched(command.requestId);

        CarpoolRequestAcceptedDomainEvent domainEvent = new CarpoolRequestAcceptedDomainEvent(
                CarpoolRequestAcceptedEvent.newBuilder()
                        .setCarpoolId(command.carpoolId)
                        .setRideRequestId(command.requestId)
                        .setPassengers(passengers)
                        .setDriverId(this.driverId)
                        .build());
        raiseEvent(domainEvent);
    }

    public void declineRequestToCarpool(DeclineCarpoolRequestCommand command) {
        ensureNotAlreadyAssigned(command.requestId);
        ensureMatched(command.requestId);

        CarpoolRequestDeclinedDomainEvent domainEvent = new CarpoolRequestDeclinedDomainEvent(
                CarpoolRequestDeclinedEvent.newBuilder()
                        .setCarpoolId(command.carpoolId)
                        .setRideRequestId(command.requestId)
                        .setDriverId(this.driverId)
                        .build());
        raiseEvent(domainEvent);
    }

    /**
     * raises CarpoolRequestInvalidatedEvent, removing the request from the pendingRideRequests list.
     * @param command
     */
    public void invalidateRequestToCarpool(InvalidateCarpoolRequestCommand command) {
        ensureNotAlreadyAssigned(command.requestId);
        ensureMatched(command.requestId);

        CarpoolRequestInvalidatedDomainEvent domainEvent = new CarpoolRequestInvalidatedDomainEvent(
                CarpoolRequestInvalidatedEvent.newBuilder()
                        .setCarpoolId(command.carpoolId)
                        .setRideRequestId(command.requestId)
                        .setReason(command.reason)
                        .setDriverId(this.driverId)
                        .build());
        raiseEvent(domainEvent);
    }

    public String getId() {
        return this.id;
    }

    public String getDriverId() {
        return this.driverId;
    }

    public int getTotalSeats() {
        return this.totalSeats;
    }

    public int getSeatsAssigned() {
        return this.seatsAssigned;
    }

    public int getAvailableSeats() {
        return this.totalSeats = this.seatsAssigned;
    }

    public Instant getArrivalTime() {
        return this.arrivalTime;
    }

    public RouteValue getRoute() {
        return this.route;
    }

    public List<String> getConfirmedRideRequestsCopy() {
        return new ArrayList<>(this.confirmedRideRequests);
    }

    public List<String> getPendingRideRequestsCopy() {
        return new ArrayList<>(this.pendingRideRequests);
    }

    public List<CarpoolDomainEvent> getUncommittedChanges() {
        return changes;
    }

    public void clearUncommittedChanges() {
        changes.clear();
    }
    
    // Raise and apply events
    private void raiseEvent(CarpoolDomainEvent event) {
        apply(event);
        changes.add(event);
    }
    
    private void apply(CarpoolDomainEvent event) {
        if (event instanceof CarpoolCreatedDomainEvent e) {
            this.id = e.event.getCarpoolId();
            this.driverId = e.event.getDriverId();
            this.totalSeats = e.event.getAvailableSeats();
            this.arrivalTime = e.event.getArrivalTime();
            this.route = RouteValue.from(e.event.getRoute());
        } else if (event instanceof CarpoolMatchedDomainEvent e) {
            this.pendingRideRequests.add(e.event.getRideRequestId());
        } else if (event instanceof CarpoolRequestAcceptedDomainEvent e) {
            this.pendingRideRequests.remove(e.event.getRideRequestId());
            this.confirmedRideRequests.add(e.event.getRideRequestId());
            this.seatsAssigned += e.event.getPassengers();
        } else if (event instanceof CarpoolRequestDeclinedDomainEvent e) {
            this.pendingRideRequests.remove(e.event.getRideRequestId());
        } else if (event instanceof CarpoolRequestInvalidatedDomainEvent e) {
            this.pendingRideRequests.remove(e.event.getRideRequestId());
        }
    }

    // guards
    private void ensureMatched(String requestId) {
        if (!this.pendingRideRequests.contains(requestId)) {
            throw new CarpoolAndRideRequestNotMatchedException(requestId, this.id);
        }
    }

    private void ensureNotAlreadyAssigned(String requestId) {
        if (this.confirmedRideRequests.contains(requestId)) {
            throw new CarpoolAndRideRequestAlreadyAssignedException(requestId, this.id);
        }
    }

    private void ensureEnoughSeats(int passengers) {
        if (this.totalSeats < this.seatsAssigned + passengers) {
            throw new DomainException("Not enough seats available");
        }
    }

}