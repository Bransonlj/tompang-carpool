package com.tompang.carpool.carpool_service.command.domain.ride_request.event;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tompang.carpool.carpool_service.command.domain.Route;
import com.tompang.carpool.carpool_service.common.DomainTopics;

public class RideRequestCreatedEvent implements RideRequestEvent {
    public final String requestId;
    public final String riderId;
    public final int passengers;

    public final LocalDateTime startTime;
    public final LocalDateTime endTime;
    public final Route route;

    @JsonCreator
    public RideRequestCreatedEvent(
        @JsonProperty("requestId") String requestId,
        @JsonProperty("riderId") String riderId,
        @JsonProperty("passengers") int passengers,

        @JsonProperty("startTime") LocalDateTime startTime,
        @JsonProperty("endTime") LocalDateTime endTime,
        @JsonProperty("route") Route route
    ) {
        this.requestId = requestId;
        this.riderId = riderId;
        this.passengers = passengers;
        this.startTime = startTime;
        this.endTime = endTime;
        this.route = route;
    }

    @Override
    public String topicName() {
        return DomainTopics.RideRequest.REQUEST_CREATED;
    }

}
