package com.tompang.carpool.carpool_service.command.domain.carpool.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tompang.carpool.carpool_service.common.DomainTopics;

public class CarpoolRequestDeclinedEvent implements CarpoolEvent {
    public final String carpoolId;
    public final String rideRequestId;
    
    @JsonCreator
    public CarpoolRequestDeclinedEvent(
        @JsonProperty("carpoolId") String carpoolId,
        @JsonProperty("rideRequestId") String rideRequestId
    ) {
        this.carpoolId = carpoolId;
        this.rideRequestId = rideRequestId;
    }

    @Override
    public String topicName() {
        return DomainTopics.Carpool.CARPOOL_REQUEST_ACCEPTED;
    }
}
