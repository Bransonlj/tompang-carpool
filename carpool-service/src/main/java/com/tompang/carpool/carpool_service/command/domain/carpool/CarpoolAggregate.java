package com.tompang.carpool.carpool_service.command.domain.carpool;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.tompang.carpool.carpool_service.command.command.carpool.CreateCarpoolCommand;
import com.tompang.carpool.carpool_service.command.command.carpool.MatchCarpoolCommand;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolCreatedEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolMatchedEvent;

public class CarpoolAggregate {

    private String id;
    private int totalSeats;
    private int seatsAssigned = 0;
    private List<String> confirmedRideRequests = new ArrayList<>();;
    private List<String> pendingRideRequests = new ArrayList<>();;
    private String driverId;

    private String origin;
    private String destination;
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
                command.arrivalTime, command.origin, command.destination));
        return carpool;
    }

    public void matchRequestToCarpool(MatchCarpoolCommand command) {
        // TODO validate
        raiseEvent(new CarpoolMatchedEvent(command.carpoolId, command.requestId));
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
            this.id = e.getCarpoolId();
            this.driverId = e.getDriverId();
            this.totalSeats = e.getAvailableSeats();
            this.arrivalTime = e.getArrivalTime();
            this.origin = e.getOrigin();
            this.destination = e.getDestination();
        } else if (event instanceof CarpoolMatchedEvent e) {
            this.pendingRideRequests.add(e.getRideRequestId());
        }
    }

}