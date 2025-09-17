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
import com.tompang.carpool.carpool_service.command.domain.exception.CarpoolAndRideRequestNotMatchedException;
import com.tompang.carpool.carpool_service.command.domain.exception.DomainException;
import com.tompang.carpool.carpool_service.command.domain.exception.CarpoolAndRideRequestAlreadyAssignedException;
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

    public String getId() {
        return this.id;
    }

    public String getRiderId() {
        return this.riderId;
    }

    public int getPassengers() {
        return this.passengers;
    }

    public List<String> getMatchedCarpoolsCopy() {
        return new ArrayList<>(this.matchedCarpools);
    }

    public Optional<String> getAssignedCarpool() {
        return this.assignedCarpool;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public RouteValue getRoute() {
        return this.route;
    }

    public RideRequestStatus getStatus() {
        return this.status;
    }

    /**
     * Returns whether RideRequest can be assigned a Carpool by checking if the status is Pending.
     * @return
     */
    public boolean canAssign() {
        return this.status == RideRequestStatus.PENDING; // TODO check assignedCarpool optional?
    }

    public List<RideRequestEvent> getUncommittedChanges() {
        return changes;
    }

    public void clearUncommittedChanges() {
        changes.clear();
    }

    public static RideRequestAggregate rehydrate(List<RideRequestEvent> history) {
        RideRequestAggregate rideRequest = new RideRequestAggregate();
        for (RideRequestEvent event : history) {
            rideRequest.apply(event);
        }
        return rideRequest;
    }

    /**
     * Command to create a ride-request with the specified details.
     * Throws exception if startTime is after endTime.
     * @param command
     * @return
     */
    public static RideRequestAggregate createRideRequest(CreateRideRequestCommand command) {
        ensureValidTimeRange(command.startTime, command.endTime);

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
        ensureNotAssigned();

        RideRequestMatchedDomainEvent domainEvent = new RideRequestMatchedDomainEvent(
                RideRequestMatchedEvent.newBuilder()
                        .setRequestId(command.requestId)
                        .setMatchedCarpoolIds(command.matchedCarpoolIds)
                        .setRiderId(this.riderId)
                        .build());
        this.raiseEvent(domainEvent);
    }

    /**
     * raises RideRequestFailedEvent, invoke when request failed (eg. no match found)
     * @param command
     */
    public void failRideRequest(FailRideRequestCommand command) {
        ensureHasNoMatches();
        ensureNotAssigned();
        
        RideRequestFailedDomainEvent domainEvent = new RideRequestFailedDomainEvent(
                RideRequestFailedEvent.newBuilder()
                        .setRequestId(command.requestId)
                        .setReason(command.reason)
                        .setRiderId(this.riderId)
                        .build());
        this.raiseEvent(domainEvent);
    }

    /**
     * raises RideRequestAcceptedEvent, with leftoverCarpoolIds: a list of matched carpools except the accepted carpool
     * @param command
     */
    public void acceptCarpoolRequest(AcceptCarpoolRequestCommand command) {
        ensureNotAssigned();
        ensureHasMatchedCarpool(command.carpoolId); 

        List<String> leftoverCarpools = this.getMatchedCarpoolsCopy();
        leftoverCarpools.remove(command.carpoolId);
        RideRequestAcceptedDomainEvent domainEvent = new RideRequestAcceptedDomainEvent(
                RideRequestAcceptedEvent.newBuilder()
                        .setRequestId(command.requestId)
                        .setCarpoolId(command.carpoolId)
                        .setLeftoverCarpoolIds(leftoverCarpools)
                        .setRiderId(this.riderId).build()
        );
        this.raiseEvent(domainEvent);
    }

    public void declineCarpoolRequest(DeclineCarpoolRequestCommand command) {
        ensureNotAssigned();
        ensureHasMatchedCarpool(command.carpoolId);
        
        RideRequestDeclineDomainEvent domainEvent = new RideRequestDeclineDomainEvent(
                RideRequestDeclinedEvent.newBuilder()
                        .setRequestId(command.requestId)
                        .setCarpoolId(command.carpoolId)
                        .setRiderId(this.riderId)
                        .build());
        this.raiseEvent(domainEvent);
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
            this.matchedCarpools.clear(); // clear matched carpools & and assign accepted carpool
            this.assignedCarpool = Optional.of(e.event.getCarpoolId());
            this.status = RideRequestStatus.ASSIGNED;
        } else if (event instanceof RideRequestDeclineDomainEvent e) {
            this.matchedCarpools.remove(e.event.getCarpoolId());
        }
    }

    // guards
    private void ensureNotAssigned() {
        if (this.getAssignedCarpool().isPresent()) {
            throw new CarpoolAndRideRequestAlreadyAssignedException(this.id);
        }
    }

    private void ensureHasMatchedCarpool(String carpoolId) {
        if (!this.matchedCarpools.contains(carpoolId)) {
            throw new CarpoolAndRideRequestNotMatchedException(this.id, carpoolId);
        }
    }

    private void ensureHasNoMatches() {
        if (!this.matchedCarpools.isEmpty()) {
            throw new DomainException("RideRequest still has pending carpools matched");
        }
    }

    private static void ensureValidTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new DomainException("invalid timerange: startTime is after endTime");
        }
    }

}
