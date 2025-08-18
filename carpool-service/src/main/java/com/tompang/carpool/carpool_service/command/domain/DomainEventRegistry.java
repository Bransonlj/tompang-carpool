package com.tompang.carpool.carpool_service.command.domain;

import java.util.Map;
import java.util.function.Function;

import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolCreatedDomainEvent;
import com.tompang.carpool.event.carpool.CarpoolCreatedEvent;

public class DomainEventRegistry {
    private static final Map<Class<?>, Function<Object, ? extends DomainEvent>> registry = Map.of(
        CarpoolCreatedEvent.class, obj -> new CarpoolCreatedDomainEvent((CarpoolCreatedEvent) obj)
    );

    @SuppressWarnings("unchecked")
    public static <T extends DomainEvent> T wrap(Object avroObject) {
        Function<Object, T> factory = (Function<Object, T>) registry.get(avroObject.getClass());
        if (factory == null) {
            throw new IllegalArgumentException("No wrapper registered for " + avroObject.getClass());
        }
        return factory.apply(avroObject);
    }
}
