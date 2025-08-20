package com.tompang.carpool.carpool_service.command.domain.carpool;

import java.time.LocalDateTime;
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
    private LocalDateTime arrivalTime;

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
        // TODO validate
        CarpoolMatchedDomainEvent domainEvent = new CarpoolMatchedDomainEvent(
            new CarpoolMatchedEvent(command.carpoolId, command.requestId)
        );
        raiseEvent(domainEvent);
    } 

    public void acceptRequestToCarpool(AcceptCarpoolRequestCommand command, int passengers) {
        // validate if got seats
        if (this.totalSeats < this.seatsAssigned + passengers) {
            throw new RuntimeException("Not enough seats available");
        }

        if (!this.pendingRideRequests.contains(command.requestId)) {
            throw new RuntimeException("Carpool and RideRequest are not matched");
        }

        if (this.confirmedRideRequests.contains(command.requestId)) {
            throw new RuntimeException("Carpool already accepted RideRequest");
        }

        CarpoolRequestAcceptedDomainEvent domainEvent = new CarpoolRequestAcceptedDomainEvent(
            new CarpoolRequestAcceptedEvent(command.carpoolId, command.requestId, passengers)
        );
        raiseEvent(domainEvent);
    }

    public void declineRequestToCarpool(DeclineCarpoolRequestCommand command) {
        if (!this.pendingRideRequests.contains(command.requestId)) {
            throw new RuntimeException("Carpool and RideRequest are not matched");
        }

        if (this.confirmedRideRequests.contains(command.requestId)) {
            throw new RuntimeException("Carpool already accepted RideRequest");
        }

        CarpoolRequestDeclinedDomainEvent domainEvent = new CarpoolRequestDeclinedDomainEvent(
            new CarpoolRequestDeclinedEvent(command.carpoolId, command.requestId)
        );
        raiseEvent(domainEvent);
    }

    /**
     * raises CarpoolRequestInvalidatedEvent, removing the request from the pendingRideRequests list.
     * @param command
     */
    public void invalidateRequestToCarpool(InvalidateCarpoolRequestCommand command) {
        if (!this.pendingRideRequests.contains(command.requestId)) {
            throw new RuntimeException("Carpool and RideRequest are not matched");
        }

        if (this.confirmedRideRequests.contains(command.requestId)) {
            throw new RuntimeException("Carpool already accepted RideRequest");
        }

        CarpoolRequestInvalidatedDomainEvent domainEvent = new CarpoolRequestInvalidatedDomainEvent(
            new CarpoolRequestInvalidatedEvent(command.carpoolId, command.requestId, command.reason)
        );
        raiseEvent(domainEvent);
    }

    public String getId() {
        return this.id;
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

}