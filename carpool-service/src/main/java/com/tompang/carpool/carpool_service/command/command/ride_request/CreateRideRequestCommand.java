package com.tompang.carpool.carpool_service.command.command.ride_request;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreateRideRequestCommand {
    public final String riderId;
    public final int passengers;
    public final LocalDateTime startTime;
    public final LocalDateTime endTime;
    public final String origin;
    public final String destiation;
}
