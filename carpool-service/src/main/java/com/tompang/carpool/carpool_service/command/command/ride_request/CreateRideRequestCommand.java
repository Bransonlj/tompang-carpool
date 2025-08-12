package com.tompang.carpool.carpool_service.command.command.ride_request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateRideRequestCommand {

    public final String riderId;
    public final int passengers;
    public final LocalDateTime startTime;
    public final LocalDateTime endTime;
    public final String origin;
    public final String destination;

    @JsonCreator
    public CreateRideRequestCommand(
        @JsonProperty("riderId") String riderId,
        @JsonProperty("passengers") int passengers,
        @JsonProperty("startTime") LocalDateTime startTime,
        @JsonProperty("endTime") LocalDateTime endTime,
        @JsonProperty("origin") String origin,
        @JsonProperty("destination") String destination
    ) {
        this.riderId = riderId;
        this.passengers = passengers;
        this.startTime = startTime;
        this.endTime = endTime;
        this.origin = origin;
        this.destination = destination;
    }
}