package com.tompang.carpool.carpool_service.command.domain.carpool.event;

import com.tompang.carpool.carpool_service.common.DomainTopics;
import com.tompang.carpool.event.carpool.CarpoolRequestAcceptedEvent;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CarpoolRequestAcceptedDomainEvent implements CarpoolDomainEvent {
    public final CarpoolRequestAcceptedEvent event;

    @Override
    public String topicName() {
        return DomainTopics.Carpool.CARPOOL_REQUEST_ACCEPTED;
    }

    @Override
    public Object getEvent() {
        return this.event;
    }
}
