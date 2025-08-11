package com.tompang.carpool.carpool_service.command.domain.ride_request.event;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tompang.carpool.carpool_service.common.DomainTopics;

import lombok.Getter;

@Getter
public class RideRequestMatchedEvent implements RideRequestEvent {
    private final String requestId;
    private final List<String> matchedCarpoolIds;

    @JsonCreator
    public RideRequestMatchedEvent(
        @JsonProperty("requestId") String requestId,
        @JsonProperty("matchedCarpoolIds") List<String> matchedCarpoolIds
    ) {
        this.requestId = requestId;
        this.matchedCarpoolIds = matchedCarpoolIds;
    }

    @Override
    public String topicName() {
        return DomainTopics.RideRequest.REQUEST_MATCHED;
    }
}
