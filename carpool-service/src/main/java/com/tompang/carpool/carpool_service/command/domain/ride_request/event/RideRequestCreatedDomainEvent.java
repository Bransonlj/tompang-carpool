package com.tompang.carpool.carpool_service.command.domain.ride_request.event;

import com.tompang.carpool.carpool_service.common.DomainTopics;
import com.tompang.carpool.event.ride_request.RideRequestCreatedEvent;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RideRequestCreatedDomainEvent implements RideRequestEvent {
    public final RideRequestCreatedEvent event;

    @Override
    public String topicName() {
        return DomainTopics.RideRequest.REQUEST_CREATED;
    }

    @Override
    public Object getEvent() {
        return this.event;
    }

}
