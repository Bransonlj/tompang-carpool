package com.tompang.carpool.carpool_service.command.command.carpool;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tompang.carpool.carpool_service.command.domain.RouteValue;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class CreateCarpoolCommand {

    @JsonCreator
    public CreateCarpoolCommand(
        @JsonProperty("driverId") String driverId,
        @JsonProperty("seats") int seats,
        @JsonProperty("arrivalTime") Instant arrivalTime,
        @JsonProperty("route") RouteValue route
    ) {
        this.driverId = driverId;
        this.seats = seats;
        this.arrivalTime = arrivalTime;
        this.route = route;
    }

    public final String driverId;
    public final int seats;
    public final Instant arrivalTime;
    public final RouteValue route;
}