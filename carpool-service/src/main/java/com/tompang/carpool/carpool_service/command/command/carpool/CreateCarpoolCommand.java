package com.tompang.carpool.carpool_service.command.command.carpool;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateCarpoolCommand {

    @JsonCreator
    public CreateCarpoolCommand(
        @JsonProperty("driverId") String driverId,
        @JsonProperty("seats") int seats,
        @JsonProperty("arrivalTime") LocalDateTime arrivalTime,
        @JsonProperty("origin") String origin,
        @JsonProperty("destination") String destination
    ) {
        this.driverId = driverId;
        this.seats = seats;
        this.arrivalTime = arrivalTime;
        this.origin = origin;
        this.destination = destination;
    }

    public final String driverId;
    public final int seats;
    public final LocalDateTime arrivalTime;
    public final String origin;
    public final String destination;
}