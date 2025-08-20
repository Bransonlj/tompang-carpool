package com.tompang.carpool.carpool_service.command.command.ride_request;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FailRideRequestCommand {
    public final String requestId;
    public final String reason;

    @Override
    public String toString() {
        return "FailRideRequestCommand{" +
                "requestId=" + requestId +
                ", reason=" + reason +
                '}';
    }
}
