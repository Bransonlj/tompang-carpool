package com.tompang.carpool.carpool_service.command.domain.carpool.event;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tompang.carpool.carpool_service.command.domain.Route;
import com.tompang.carpool.carpool_service.common.DomainTopics;

public class CarpoolCreatedEvent implements CarpoolEvent {

    public final String carpoolId;
    public final int availableSeats;
    public final String driverId;

    public final LocalDateTime arrivalTime;
    public final Route route;


    @JsonCreator    
    public CarpoolCreatedEvent(
        @JsonProperty("carpoolId") String carpoolId,
        @JsonProperty("availableSeats") int availableSeats,
        @JsonProperty("driverId") String driverId,
        @JsonProperty("arrivalTime") LocalDateTime arrivalTime,
        @JsonProperty("route") Route route
    ) {
        this.carpoolId = carpoolId;
        this.availableSeats = availableSeats;
        this.driverId = driverId;
        this.arrivalTime = arrivalTime;
        this.route = route;
    }

    @Override
    public String topicName() {
        return DomainTopics.Carpool.CARPOOL_CREATED;
    }
}
