package com.tompang.carpool.carpool_service.command.domain.carpool.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tompang.carpool.carpool_service.common.DomainTopics;

import lombok.Getter;

@Getter
public class CarpoolMatchedEvent implements CarpoolEvent {

    private final String carpoolId;
    private final String rideRequestId;

    @JsonCreator
    public CarpoolMatchedEvent(
        @JsonProperty("carpoolId") String carpoolId,
        @JsonProperty("rideRequestId") String rideRequestId
    ) {
        this.carpoolId = carpoolId;
        this.rideRequestId = rideRequestId;
    }

    @Override
    public String topicName() {
        return DomainTopics.Carpool.CARPOOL_MATCHED;
    }

}
