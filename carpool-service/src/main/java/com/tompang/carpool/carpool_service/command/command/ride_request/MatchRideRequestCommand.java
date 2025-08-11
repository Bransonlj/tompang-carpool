package com.tompang.carpool.carpool_service.command.command.ride_request;

import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MatchRideRequestCommand {
    public final String requestId;
    public final List<String> matchedCarpoolIds;
}
