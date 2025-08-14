package com.tompang.carpool.carpool_service.command.domain.ride_request.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tompang.carpool.carpool_service.common.DomainTopics;

public class RideRequestDeclinedEvent implements RideRequestEvent {
    public final String carpoolId;
    public final String requestId;

    @JsonCreator
    public RideRequestDeclinedEvent(
        @JsonProperty("requestId") String requestId,
        @JsonProperty("carpoolId") String carpoolId
    ) {
        this.requestId = requestId;
        this.carpoolId = carpoolId;
    }

    @Override
    public String topicName() {
        return DomainTopics.RideRequest.REQUEST_DECLINED;
    }

}
