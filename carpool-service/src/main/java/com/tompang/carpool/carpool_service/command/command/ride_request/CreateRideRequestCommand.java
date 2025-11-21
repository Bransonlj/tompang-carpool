package com.tompang.carpool.carpool_service.command.command.ride_request;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tompang.carpool.carpool_service.command.domain.RouteValue;

import lombok.Builder;

@Builder
public class CreateRideRequestCommand {

    public final String riderId;
    public final int passengers;
    public final Instant startTime;
    public final Instant endTime;
    public final RouteValue route;

    @JsonCreator
    public CreateRideRequestCommand(
        @JsonProperty("riderId") String riderId,
        @JsonProperty("passengers") int passengers,
        @JsonProperty("startTime") Instant startTime,
        @JsonProperty("endTime") Instant endTime,
        @JsonProperty("route") RouteValue route
    ) {
        this.riderId = riderId;
        this.passengers = passengers;
        this.startTime = startTime;
        this.endTime = endTime;
        this.route = route;
    }
}