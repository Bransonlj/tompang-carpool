package com.tompang.carpool.carpool_service.command.domain.ride_request.event;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tompang.carpool.carpool_service.common.DomainTopics;

import lombok.Getter;

@Getter
public class RideRequestCreatedEvent implements RideRequestEvent {
    private final String requestId;
    private final String riderId;
    private final int passengers;

    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final String origin;
    private final String destination;

    @JsonCreator
    public RideRequestCreatedEvent(
        @JsonProperty("requestId") String requestId,
        @JsonProperty("riderId") String riderId,
        @JsonProperty("passengers") int passengers,

        @JsonProperty("startTime") LocalDateTime startTime,
        @JsonProperty("endTime") LocalDateTime endTime,
        @JsonProperty("origin") String origin,
        @JsonProperty("destination") String destination
    ) {
        this.requestId = requestId;
        this.riderId = riderId;
        this.passengers = passengers;
        this.startTime = startTime;
        this.endTime = endTime;
        this.origin = origin;
        this.destination = destination;
    }

    @Override
    public String topicName() {
        return DomainTopics.RideRequest.REQUEST_CREATED;
    }

}
