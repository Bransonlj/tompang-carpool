package com.tompang.carpool.carpool_service.command.domain.ride_request.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tompang.carpool.carpool_service.common.DomainTopics;

/**
 * Event emitted when no matching carpools are found
 */
public class RideRequestFailedEvent implements RideRequestEvent {

    public final String requestId;
    public final String reason;

    @JsonCreator
    public RideRequestFailedEvent(
        @JsonProperty("requestId") String requestId,
        @JsonProperty("reason") String reason
    ) {
        this.requestId = requestId;
        this.reason = reason;
    }

    @Override
    public String topicName() {
        return DomainTopics.RideRequest.REQUEST_FAILED;
    }

}
