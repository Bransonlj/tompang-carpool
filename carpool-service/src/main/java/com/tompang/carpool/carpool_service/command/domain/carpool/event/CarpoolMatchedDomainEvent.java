package com.tompang.carpool.carpool_service.command.domain.carpool.event;

import com.tompang.carpool.carpool_service.common.DomainTopics;
import com.tompang.carpool.event.carpool.CarpoolMatchedEvent;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CarpoolMatchedDomainEvent implements CarpoolDomainEvent {

    public final CarpoolMatchedEvent event;

    @Override
    public String topicName() {
        return DomainTopics.Carpool.CARPOOL_MATCHED;
    }

    @Override
    public Object getEvent() {
        return this.event;
    }

}
