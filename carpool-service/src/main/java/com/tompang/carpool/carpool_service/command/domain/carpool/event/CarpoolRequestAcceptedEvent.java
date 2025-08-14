package com.tompang.carpool.carpool_service.command.domain.carpool.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tompang.carpool.carpool_service.common.DomainTopics;

public class CarpoolRequestAcceptedEvent implements CarpoolEvent {
    public final String carpoolId;
    public final String rideRequestId;
    public final int passengers;
    
    @JsonCreator
    public CarpoolRequestAcceptedEvent(
        @JsonProperty("carpoolId") String carpoolId,
        @JsonProperty("rideRequestId") String rideRequestId,
        @JsonProperty("passengers") int passengers
    ) {
        this.carpoolId = carpoolId;
        this.rideRequestId = rideRequestId;
        this.passengers = passengers;
    }

    @Override
    public String topicName() {
        return DomainTopics.Carpool.CARPOOL_REQUEST_ACCEPTED;
    }
}
