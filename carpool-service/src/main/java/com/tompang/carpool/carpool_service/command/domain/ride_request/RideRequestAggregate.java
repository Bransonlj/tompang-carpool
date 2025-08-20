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
import com.tompang.carpool.carpool_service.command.domain.RouteValue;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestAcceptedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestCreatedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestDeclineDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestFailedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestMatchedDomainEvent;
import com.tompang.carpool.event.ride_request.RideRequestAcceptedEvent;
import com.tompang.carpool.event.ride_request.RideRequestCreatedEvent;
import com.tompang.carpool.event.ride_request.RideRequestDeclinedEvent;
import com.tompang.carpool.event.ride_request.RideRequestFailedEvent;
import com.tompang.carpool.event.ride_request.RideRequestMatchedEvent;

public class RideRequestAggregate {

    private String id;
    private String riderId;
    private int passengers;
    private List<String> matchedCarpools = new ArrayList<>();
    private Optional<String> assignedCarpool = Optional.empty();
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private RouteValue route;
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
        RideRequestCreatedDomainEvent domainEvent = new RideRequestCreatedDomainEvent(
            new RideRequestCreatedEvent(UUID.randomUUID().toString(), command.riderId, command.passengers, 
                command.startTime, 
                command.endTime, 
                command.route.toSchemaRoute()
            )
        );
        rideRequest.raiseEvent(domainEvent);
        return rideRequest;
    }

    public void matchRideRequest(MatchRideRequestCommand command) {
        // TODO perform any validation
        RideRequestMatchedDomainEvent domainEvent = new RideRequestMatchedDomainEvent(
            new RideRequestMatchedEvent(command.requestId, command.matchedCarpoolIds)
        );
        this.raiseEvent(domainEvent);
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
        
        RideRequestFailedDomainEvent domainEvent = new RideRequestFailedDomainEvent(
            new RideRequestFailedEvent(command.requestId, command.reason)
        );
        this.raiseEvent(domainEvent);
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

        RideRequestAcceptedDomainEvent domainEvent = new RideRequestAcceptedDomainEvent(
            new RideRequestAcceptedEvent(command.requestId, command.carpoolId)
        );
        this.raiseEvent(domainEvent);
    }

    public void declineCarpoolRequest(DeclineCarpoolRequestCommand command) {
        if (!this.matchedCarpools.contains(command.carpoolId)) {
            throw new RuntimeException("Carpool and RideRequest do not match");
        }

        if (this.assignedCarpool.isPresent()) {
            throw new RuntimeException("RideRequest already assigned to a Carpool");
        }

        RideRequestDeclineDomainEvent domainEvent = new RideRequestDeclineDomainEvent(
            new RideRequestDeclinedEvent(command.requestId, command.carpoolId)
        );
        this.raiseEvent(domainEvent);
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
        if (event instanceof RideRequestCreatedDomainEvent e) {
            this.id = e.event.getRequestId();
            this.riderId = e.event.getRiderId();
            this.passengers = e.event.getPassengers();
            this.startTime = e.event.getStartTime();
            this.endTime = e.event.getEndTime();
            this.route = RouteValue.from(e.event.getRoute());
        } else if (event instanceof RideRequestMatchedDomainEvent e) {
            this.matchedCarpools.addAll(e.event.getMatchedCarpoolIds());
        } else if (event instanceof RideRequestFailedDomainEvent) {
            this.status = RideRequestStatus.FAILED;
        } else if (event instanceof RideRequestAcceptedDomainEvent e) {
            this.assignedCarpool = Optional.of(e.event.getCarpoolId());
            this.status = RideRequestStatus.ASSIGNED;
            this.matchedCarpools.clear(); // clear list of matched carpools
        } else if (event instanceof RideRequestDeclineDomainEvent e) {
            this.matchedCarpools.remove(e.event.getCarpoolId());
        }
    }

}
