package com.tompang.carpool.carpool_service.command.domain.carpool.event;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tompang.carpool.carpool_service.common.DomainTopics;

import lombok.Getter;

@Getter
public class CarpoolCreatedEvent implements CarpoolEvent {

    private final String carpoolId;
    private final int availableSeats;
    private final String driverId;

    private LocalDateTime arrivalTime;
    private String origin;
    private String destination;


    @JsonCreator    
    public CarpoolCreatedEvent(
        @JsonProperty("carpoolId") String carpoolId,
        @JsonProperty("availableSeats") int availableSeats,
        @JsonProperty("driverId") String driverId,
        @JsonProperty("arrivalTime") LocalDateTime arrivalTime,
        @JsonProperty("origin") String origin,
        @JsonProperty("destination") String destination
    ) {
        this.carpoolId = carpoolId;
        this.availableSeats = availableSeats;
        this.driverId = driverId;
        this.arrivalTime = arrivalTime;
        this.origin = origin;
        this.destination = destination;
    }

    @Override
    public String topicName() {
        return DomainTopics.Carpool.CARPOOL_CREATED;
    }
}
