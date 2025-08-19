package com.tompang.carpool.carpool_service.command.domain.ride_request.event;

import com.tompang.carpool.carpool_service.common.DomainTopics;
import com.tompang.carpool.event.ride_request.RideRequestDeclinedEvent;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RideRequestDeclineDomainEvent implements RideRequestEvent {
    public final RideRequestDeclinedEvent event;

    @Override
    public String topicName() {
        return DomainTopics.RideRequest.REQUEST_DECLINED;
    }

    @Override
    public Object getEvent() {
        return this.event;
    }

}
