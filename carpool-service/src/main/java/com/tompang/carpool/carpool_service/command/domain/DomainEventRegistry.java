package com.tompang.carpool.carpool_service.command.domain;

import java.util.Map;
import java.util.function.Function;

import org.apache.avro.specific.SpecificRecord;

import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolCreatedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolMatchedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolRequestAcceptedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolRequestDeclinedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolRequestInvalidatedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestAcceptedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestCreatedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestDeclineDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestFailedDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestMatchedDomainEvent;
import com.tompang.carpool.event.carpool.CarpoolCreatedEvent;
import com.tompang.carpool.event.carpool.CarpoolMatchedEvent;
import com.tompang.carpool.event.carpool.CarpoolRequestAcceptedEvent;
import com.tompang.carpool.event.carpool.CarpoolRequestDeclinedEvent;
import com.tompang.carpool.event.carpool.CarpoolRequestInvalidatedEvent;
import com.tompang.carpool.event.ride_request.RideRequestAcceptedEvent;
import com.tompang.carpool.event.ride_request.RideRequestCreatedEvent;
import com.tompang.carpool.event.ride_request.RideRequestDeclinedEvent;
import com.tompang.carpool.event.ride_request.RideRequestFailedEvent;
import com.tompang.carpool.event.ride_request.RideRequestMatchedEvent;

/**
 * Registry responsible for the mapping of the Avro schema event class with the wrapper DomainEvent class.
 */
public class DomainEventRegistry {
    private static final Map<Class<? extends SpecificRecord>, Function<Object, ? extends DomainEvent>> registry = Map.of(
        // Carpool events
        CarpoolCreatedEvent.class, obj -> new CarpoolCreatedDomainEvent((CarpoolCreatedEvent) obj),
        CarpoolMatchedEvent.class, obj -> new CarpoolMatchedDomainEvent((CarpoolMatchedEvent) obj),
        CarpoolRequestAcceptedEvent.class, obj -> new CarpoolRequestAcceptedDomainEvent((CarpoolRequestAcceptedEvent) obj),
        CarpoolRequestDeclinedEvent.class, obj -> new CarpoolRequestDeclinedDomainEvent((CarpoolRequestDeclinedEvent) obj),
        CarpoolRequestInvalidatedEvent.class, obj -> new CarpoolRequestInvalidatedDomainEvent((CarpoolRequestInvalidatedEvent) obj),

        // RideRequest events
        RideRequestAcceptedEvent.class, obj -> new RideRequestAcceptedDomainEvent((RideRequestAcceptedEvent) obj),
        RideRequestCreatedEvent.class, obj -> new RideRequestCreatedDomainEvent((RideRequestCreatedEvent) obj),
        RideRequestDeclinedEvent.class, obj -> new RideRequestDeclineDomainEvent((RideRequestDeclinedEvent) obj),
        RideRequestFailedEvent.class, obj -> new RideRequestFailedDomainEvent((RideRequestFailedEvent) obj),
        RideRequestMatchedEvent.class, obj -> new RideRequestMatchedDomainEvent((RideRequestMatchedEvent) obj)
    );

    @SuppressWarnings("unchecked")
    public static <T extends DomainEvent> T wrap(Object avroObject) throws IllegalArgumentException {
        Function<Object, T> factory = (Function<Object, T>) registry.get(avroObject.getClass());
        if (factory == null) {
            throw new IllegalArgumentException("No wrapper registered for " + avroObject.getClass());
        }
        return factory.apply(avroObject);
    }
}
