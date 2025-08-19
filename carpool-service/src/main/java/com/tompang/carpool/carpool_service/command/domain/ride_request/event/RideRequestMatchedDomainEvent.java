package com.tompang.carpool.carpool_service.command.domain.ride_request.event;

import com.tompang.carpool.carpool_service.common.DomainTopics;
import com.tompang.carpool.event.ride_request.RideRequestMatchedEvent;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RideRequestMatchedDomainEvent implements RideRequestEvent {
    public final RideRequestMatchedEvent event;

    @Override
    public String topicName() {
        return DomainTopics.RideRequest.REQUEST_MATCHED;
    }

    @Override
    public Object getEvent() {
        return this.event;
    }
}
