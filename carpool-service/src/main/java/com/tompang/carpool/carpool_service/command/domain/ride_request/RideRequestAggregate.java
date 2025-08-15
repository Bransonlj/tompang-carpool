package com.tompang.carpool.carpool_service.command.domain.ride_request;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.tompang.carpool.carpool_service.command.command.carpool.AcceptCarpoolRequestCommand;
import com.tompang.carpool.carpool_service.command.command.carpool.DeclineCarpoolRequestCommand;
import com.tompang.carpool.carpool_service.command.command.ride_request.CreateRideRequestCommand;
import com.tompang.carpool.carpool_service.command.command.ride_request.FailRideRequestCommand;
import com.tompang.carpool.carpool_service.command.command.ride_request.MatchRideRequestCommand;
import com.tompang.carpool.carpool_service.command.domain.Route;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestAcceptedEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestCreatedEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestDeclinedEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestFailedEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestMatchedEvent;

public class RideRequestAggregate {

    private String id;
    private String riderId;
    private int passengers;
    private List<String> matchedCarpools = new ArrayList<>();
    private Optional<String> assignedCarpool = Optional.empty();
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Route route;
    private RideRequestStatus status = RideRequestStatus.PENDING;

    // List of new events to be persisted
    private final List<RideRequestEvent> changes = new ArrayList<>();

    public static RideRequestAggregate rehydrate(List<RideRequestEvent> history) {
        RideRequestAggregate rideRequest = new RideRequestAggregate();
        for (RideRequestEvent event : history) {
            rideRequest.apply(event);
        }
        return rideRequest;
    }

    public static RideRequestAggregate createRideRequest(CreateRideRequestCommand command) {
        RideRequestAggregate rideRequest = new RideRequestAggregate();
        rideRequest.raiseEvent(new RideRequestCreatedEvent(UUID.randomUUID().toString(), command.riderId,
                command.passengers, command.startTime, command.endTime, command.route));
        return rideRequest;
    }

    public void matchRideRequest(MatchRideRequestCommand command) {
        // TODO perform any validation
        this.raiseEvent(new RideRequestMatchedEvent(command.requestId, command.matchedCarpoolIds));
    }

    /**
     * raises RideRequestFailedEvent, invoke when request failed (eg. no match found)
     * @param command
     */
    public void failRideRequest(FailRideRequestCommand command) {
        if (!this.matchedCarpools.isEmpty()) {
            throw new RuntimeException("RideRequest still has pending carpools matched");
        }

        if (this.assignedCarpool.isPresent()) {
            throw new RuntimeException("RideRequest already assigned to a Carpool");
        }
        
        this.raiseEvent(new RideRequestFailedEvent(command.requestId, command.reason));
    }

    /**
     * raises RideRequestAcceptedEvent
     * @param command
     */
    public void acceptCarpoolRequest(AcceptCarpoolRequestCommand command) {
        if (!this.matchedCarpools.contains(command.carpoolId)) {
            throw new RuntimeException("Carpool and RideRequest do not match");
        }

        if (this.assignedCarpool.isPresent()) {
            throw new RuntimeException("RideRequest already assigned to a Carpool");
        }

        this.raiseEvent(new RideRequestAcceptedEvent(command.requestId, command.carpoolId));
    }

    public void declineCarpoolRequest(DeclineCarpoolRequestCommand command) {
        if (!this.matchedCarpools.contains(command.carpoolId)) {
            throw new RuntimeException("Carpool and RideRequest do not match");
        }

        if (this.assignedCarpool.isPresent()) {
            throw new RuntimeException("RideRequest already assigned to a Carpool");
        }

        this.raiseEvent(new RideRequestDeclinedEvent(command.requestId, command.carpoolId));
    }

    public String getId() {
        return this.id;
    }

    public RideRequestStatus getStatus() {
        return this.status;
    }

    public int getPassengers() {
        return this.passengers;
    }

    public List<String> getMatchedCarpoolsCopy() {
        return new ArrayList<>(this.matchedCarpools);
    }

    /**
     * Returns whether RideRequest can be assigned a Carpool by checking if the status is Pending.
     * @return
     */
    public boolean canAssign() {
        return this.status == RideRequestStatus.PENDING;
    }

    public List<RideRequestEvent> getUncommittedChanges() {
        return changes;
    }

    public void clearUncommittedChanges() {
        changes.clear();
    }
    
    // Raise and apply events
    private void raiseEvent(RideRequestEvent event) {
        apply(event);
        changes.add(event);
    }

    private void apply(RideRequestEvent event) {
        if (event instanceof RideRequestCreatedEvent e) {
            this.id = e.requestId;
            this.riderId = e.riderId;
            this.passengers = e.passengers;
            this.startTime = e.startTime;
            this.endTime = e.endTime;
            this.route = e.route;
        } else if (event instanceof RideRequestMatchedEvent e) {
            this.matchedCarpools.addAll(e.matchedCarpoolIds);
        } else if (event instanceof RideRequestFailedEvent) {
            this.status = RideRequestStatus.FAILED;
        } else if (event instanceof RideRequestAcceptedEvent e) {
            this.assignedCarpool = Optional.of(e.carpoolId);
            this.status = RideRequestStatus.ASSIGNED;
            this.matchedCarpools = new ArrayList<>(); // clear list of matched carpools
        } else if (event instanceof RideRequestDeclinedEvent e) {
            this.matchedCarpools.remove(e.carpoolId);
        }
    }

}
