package com.tompang.carpool.carpool_service.command.domain.ride_request.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tompang.carpool.carpool_service.common.DomainTopics;

/**
 * Event when ride request is accepted by a carpool, assigning them together.
 */
public class RideRequestAcceptedEvent implements RideRequestEvent {

    public final String carpoolId;
    public final String requestId;

    @JsonCreator
    public RideRequestAcceptedEvent(
        @JsonProperty("requestId") String requestId,
        @JsonProperty("carpoolId") String carpoolId
    ) {
        this.requestId = requestId;
        this.carpoolId = carpoolId;
    }

    @Override
    public String topicName() {
        return DomainTopics.RideRequest.REQUEST_ACCEPTED;
    }

}
