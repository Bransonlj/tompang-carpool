package com.tompang.carpool.carpool_service.command.command.ride_request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public class MatchRideRequestCommand {

    public final String requestId;
    public final List<String> matchedCarpoolIds;

    @JsonCreator
    public MatchRideRequestCommand(
        @JsonProperty("requestId") String requestId,
        @JsonProperty("matchedCarpoolIds") List<String> matchedCarpoolIds
    ) {
        this.requestId = requestId;
        this.matchedCarpoolIds = matchedCarpoolIds;
    }
}