package com.tompang.carpool.carpool_service.command.domain.carpool.event;

import com.tompang.carpool.carpool_service.common.DomainTopics;
import com.tompang.carpool.event.carpool.CarpoolRequestInvalidatedEvent;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CarpoolRequestInvalidatedDomainEvent implements CarpoolDomainEvent {
    public final CarpoolRequestInvalidatedEvent event;

    @Override
    public String topicName() {
        return DomainTopics.Carpool.CARPOOL_REQUEST_ACCEPTED;
    }

    @Override
    public Object getEvent() {
        return this.event;
    }
}
