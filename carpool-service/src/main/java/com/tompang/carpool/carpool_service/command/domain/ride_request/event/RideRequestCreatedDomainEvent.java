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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Same object
        if (obj == null || getClass() != obj.getClass()) return false; // Different type

        RideRequestCreatedDomainEvent domainEvent = (RideRequestCreatedDomainEvent) obj;

        return domainEvent != null && event.equals(domainEvent.event);
    }

    @Override
    public int hashCode() {
        return event.hashCode();
    }

}
