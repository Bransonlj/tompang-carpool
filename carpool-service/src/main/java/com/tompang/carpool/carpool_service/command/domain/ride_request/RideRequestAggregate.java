package com.tompang.carpool.carpool_service.command.domain.ride_request;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.tompang.carpool.carpool_service.command.command.ride_request.CreateRideRequestCommand;
import com.tompang.carpool.carpool_service.command.command.ride_request.MatchRideRequestCommand;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestCreatedEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestMatchedEvent;

public class RideRequestAggregate {

    class AssignedCarpool {
        private boolean isAssigned;
        private String assignedCarpoolId;

        public AssignedCarpool() {
            this.isAssigned = false;
        }

        public void assign(String carpoolId) {
            this.isAssigned = true;
            assignedCarpoolId = carpoolId;
        }

        public boolean getIsAssigned() {
            return this.isAssigned;
        }

        public String getAssignedCarpoolId() {
            return this.assignedCarpoolId;
        }
    }

    private String id;
    private String riderId;
    private int passengers;
    private List<String> matchedCarpools = new ArrayList<>();
    private Optional<AssignedCarpool> assignedCarpool = Optional.empty();
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String origin;
    private String destiation;

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
                command.passengers, command.startTime, command.endTime, command.origin, command.destiation));
        return rideRequest;
    }

    public void matchRideRequest(MatchRideRequestCommand command) {
        // TODO perform any validation
        this.raiseEvent(new RideRequestMatchedEvent(command.requestId, command.matchedCarpoolIds));
    }

    public String getId() {
        return this.id;
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
            this.id = e.getRequestId();
            this.riderId = e.getRiderId();
            this.passengers = e.getPassengers();
            this.startTime = e.getStartTime();
            this.endTime = e.getEndTime();
            this.origin = e.getOrigin();
            this.destiation = e.getDestination();
        } else if (event instanceof RideRequestMatchedEvent e) {
            this.matchedCarpools.addAll(e.getMatchedCarpoolIds());
        }
    }

}
