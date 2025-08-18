package com.tompang.carpool.carpool_service.command.domain.carpool.event;

import com.tompang.carpool.carpool_service.common.DomainTopics;
import com.tompang.carpool.event.carpool.CarpoolCreatedEvent;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CarpoolCreatedDomainEvent implements CarpoolEvent {

    public final CarpoolCreatedEvent event;

    @Override
    public String topicName() {
        return DomainTopics.Carpool.CARPOOL_CREATED;
    }

    @Override
    public Object getEvent() {
        return this.event;
    }
}
