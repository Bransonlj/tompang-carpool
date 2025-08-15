package com.tompang.carpool.carpool_service.command.command.carpool;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tompang.carpool.carpool_service.command.domain.Route;

public class CreateCarpoolCommand {

    @JsonCreator
    public CreateCarpoolCommand(
        @JsonProperty("driverId") String driverId,
        @JsonProperty("seats") int seats,
        @JsonProperty("arrivalTime") LocalDateTime arrivalTime,
        @JsonProperty("route") Route route
    ) {
        this.driverId = driverId;
        this.seats = seats;
        this.arrivalTime = arrivalTime;
        this.route = route;
    }

    public final String driverId;
    public final int seats;
    public final LocalDateTime arrivalTime;
    public final Route route;
}