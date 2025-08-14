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
import com.tompang.carpool.carpool_service.command.domain.Route;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolCreatedEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolMatchedEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolRequestAcceptedEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolRequestDeclinedEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolRequestInvalidatedEvent;

public class CarpoolAggregate {

    private String id;
    private int totalSeats;
    private int seatsAssigned = 0;
    private List<String> confirmedRideRequests = new ArrayList<>();;
    private List<String> pendingRideRequests = new ArrayList<>();;
    private String driverId;

    private Route route;
    private LocalDateTime arrivalTime;

    // List of new events to be persisted
    private final List<CarpoolEvent> changes = new ArrayList<>();

    public static CarpoolAggregate rehydrate(List<CarpoolEvent> history) {
        CarpoolAggregate carpool = new CarpoolAggregate();
        for (CarpoolEvent event : history) {
            carpool.apply(event);
        }
        return carpool;
    }

    public static CarpoolAggregate createCarpool(CreateCarpoolCommand command) {
        CarpoolAggregate carpool = new CarpoolAggregate();
        carpool.raiseEvent(new CarpoolCreatedEvent(UUID.randomUUID().toString(), command.seats, command.driverId,
                command.arrivalTime, new Route(command.origin, command.destination)));
        return carpool;
    }

    public void matchRequestToCarpool(MatchCarpoolCommand command) {
        // TODO validate
        raiseEvent(new CarpoolMatchedEvent(command.carpoolId, command.requestId));
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

        raiseEvent(new CarpoolRequestAcceptedEvent(command.carpoolId, command.requestId, passengers));
    }

    public void declineRequestToCarpool(DeclineCarpoolRequestCommand command) {
        if (!this.pendingRideRequests.contains(command.requestId)) {
            throw new RuntimeException("Carpool and RideRequest are not matched");
        }

        if (this.confirmedRideRequests.contains(command.requestId)) {
            throw new RuntimeException("Carpool already accepted RideRequest");
        }

        raiseEvent(new CarpoolRequestDeclinedEvent(command.carpoolId, command.requestId));
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

        raiseEvent(new CarpoolRequestInvalidatedEvent(command.carpoolId, command.requestId, command.reason));
    }

    public String getId() {
        return this.id;
    }

    public List<CarpoolEvent> getUncommittedChanges() {
        return changes;
    }

    public void clearUncommittedChanges() {
        changes.clear();
    }
    
    // Raise and apply events
    private void raiseEvent(CarpoolEvent event) {
        apply(event);
        changes.add(event);
    }
    
    private void apply(CarpoolEvent event) {
        if (event instanceof CarpoolCreatedEvent e) {
            this.id = e.carpoolId;
            this.driverId = e.driverId;
            this.totalSeats = e.availableSeats;
            this.arrivalTime = e.arrivalTime;
            this.route = e.route;
        } else if (event instanceof CarpoolMatchedEvent e) {
            this.pendingRideRequests.add(e.rideRequestId);
        } else if (event instanceof CarpoolRequestAcceptedEvent e) {
            this.pendingRideRequests.remove(e.rideRequestId);
            this.confirmedRideRequests.add(e.rideRequestId);
            this.seatsAssigned += e.passengers;
        } else if (event instanceof CarpoolRequestDeclinedEvent e) {
            this.pendingRideRequests.remove(e.rideRequestId);
        } else if (event instanceof CarpoolRequestInvalidatedEvent e) {
            this.pendingRideRequests.remove(e.rideRequestId);
        }
    }

}