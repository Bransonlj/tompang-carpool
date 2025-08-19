package com.tompang.carpool.carpool_service.command.domain.ride_request.event;

import com.tompang.carpool.carpool_service.common.DomainTopics;
import com.tompang.carpool.event.ride_request.RideRequestFailedEvent;

import lombok.AllArgsConstructor;

/**
 * Event emitted when no matching carpools are found
 */
@AllArgsConstructor
public class RideRequestFailedDomainEvent implements RideRequestEvent {

    public final RideRequestFailedEvent event;

    @Override
    public String topicName() {
        return DomainTopics.RideRequest.REQUEST_FAILED;
    }

    @Override
    public Object getEvent() {
        return this.event;
    }

}
