package com.tompang.carpool.carpool_service.command.command.carpool;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreateCarpoolCommand {
    public final String driverId;
    public final int seats;
    public final LocalDateTime arrivalTime;
    public final String origin;
    public final String destination;
}
