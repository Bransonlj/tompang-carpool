package com.tompang.carpool.carpool_service.command.domain.ride_request.event;

import com.tompang.carpool.carpool_service.common.DomainTopics;
import com.tompang.carpool.event.ride_request.RideRequestAcceptedEvent;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RideRequestAcceptedDomainEvent implements RideRequestEvent {

    public final RideRequestAcceptedEvent event;

    @Override
    public String topicName() {
        return DomainTopics.RideRequest.REQUEST_ACCEPTED;
    }

    @Override
    public Object getEvent() {
        return this.event;
    }

}
