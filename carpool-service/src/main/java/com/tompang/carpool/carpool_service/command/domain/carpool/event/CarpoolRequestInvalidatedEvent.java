package com.tompang.carpool.carpool_service.command.domain.carpool.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tompang.carpool.carpool_service.common.DomainTopics;

public class CarpoolRequestInvalidatedEvent implements CarpoolEvent {
    public final String carpoolId;
    public final String rideRequestId;
    public final String reason;
    
    @JsonCreator
    public CarpoolRequestInvalidatedEvent(
        @JsonProperty("carpoolId") String carpoolId,
        @JsonProperty("rideRequestId") String rideRequestId,
        @JsonProperty("reason") String reason
    ) {
        this.carpoolId = carpoolId;
        this.rideRequestId = rideRequestId;
        this.reason = reason;
    }

    @Override
    public String topicName() {
        return DomainTopics.Carpool.CARPOOL_REQUEST_ACCEPTED;
    }
}
