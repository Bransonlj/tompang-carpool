package com.tompang.carpool.carpool_service.command.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tompang.carpool.carpool_service.command.domain.DomainEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolCreatedEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolMatchedEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolRequestAcceptedEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolRequestDeclinedEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolRequestInvalidatedEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestAcceptedEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestCreatedEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestDeclinedEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestFailedEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestMatchedEvent;
import com.tompang.carpool.carpool_service.common.kurrent.StreamId;

import io.kurrent.dbclient.AppendToStreamOptions;
import io.kurrent.dbclient.EventData;
import io.kurrent.dbclient.KurrentDBClient;
import io.kurrent.dbclient.ReadResult;
import io.kurrent.dbclient.ReadStreamOptions;
import io.kurrent.dbclient.RecordedEvent;
import io.kurrent.dbclient.ResolvedEvent;
import io.kurrent.dbclient.StreamState;

@Repository
public class EventRepository {

    public static final class CarpoolConstants {
        public static final String STREAM_PREFIX = "carpool";
        public static final Map<String, Class<? extends CarpoolEvent>> EVENT_TYPE_MAP = Stream
                .<Class<? extends CarpoolEvent>>of(
                        CarpoolCreatedEvent.class,
                        CarpoolMatchedEvent.class,
                        CarpoolRequestAcceptedEvent.class,
                        CarpoolRequestDeclinedEvent.class,
                        CarpoolRequestInvalidatedEvent.class
                    )
                .collect(Collectors.toUnmodifiableMap(Class::getName, c -> c));
    }

    public static final class RideRequestConstants {
        public static final String STREAM_PREFIX = "ride-request";
        public static final Map<String, Class<? extends RideRequestEvent>> EVENT_TYPE_MAP = Stream
                .<Class<? extends RideRequestEvent>>of(
                    RideRequestCreatedEvent.class,
                    RideRequestMatchedEvent.class,
                    RideRequestFailedEvent.class,
                    RideRequestAcceptedEvent.class,
                    RideRequestDeclinedEvent.class
                    )
                .collect(Collectors.toUnmodifiableMap(Class::getName, c -> c));
    }

    private final Logger logger = LoggerFactory.getLogger(EventRepository.class);
    private final KurrentDBClient client;
    private final ObjectMapper objectMapper;

    public EventRepository(KurrentDBClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    public <T extends DomainEvent> T deserializeEvent(ResolvedEvent resolvedEvent, Map<String, Class<? extends T>> eventTypeMap) {
        RecordedEvent recordedEvent = resolvedEvent.getOriginalEvent();
        String eventType = recordedEvent.getEventType();
        byte[] json = recordedEvent.getEventData();
        Class<? extends T> clazz = eventTypeMap.get(eventType);

        if (clazz == null) {
            throw new RuntimeException(String.format(
                "Unable to map KurrentDb eventType to a domain event class '$s'. Please ensure the domain event class has a mapping in EVENT_TYPE_MAP.",
                eventType));
        }

        try {
            T eventObj = objectMapper.readValue(json, clazz);
            return eventObj;
        } catch (IOException e) {
            throw new RuntimeException(String.format("Failed to deserialize event type '%s': %s%n", eventType, e.getMessage()));
        }
    }

    public <T extends DomainEvent> List<T> deserializeEvents(List<ResolvedEvent> resolvedEvents, Map<String, Class<? extends T>> eventTypeMap) {
        List<T> domainEvents = new ArrayList<>();
        for (ResolvedEvent resolvedEvent : resolvedEvents) {
            try {
                domainEvents.add(deserializeEvent(resolvedEvent, eventTypeMap));
            } catch (Exception e) {
                this.logger.warn(e.getMessage());
            } 
        }

        return domainEvents;
    }

    public void appendEvents(StreamId streamId, List<? extends DomainEvent> events) {
        appendEvents(streamId, events, null);
    }

    public void appendEvents(StreamId streamId, List<? extends DomainEvent> events, Long revision) {
        List<EventData> eventDatas = events.stream()
            .map(event -> {
                try {
                    byte[] jsonBytes = this.objectMapper.writeValueAsBytes(event);
                    return EventData
                        .builderAsJson(
                            UUID.randomUUID(), 
                            event.getClass().getName(),
                            jsonBytes
                        ).build();
                } catch (JsonProcessingException exception) {
                    throw new RuntimeException("Failed to serialize event: " + event.getClass(), exception);
                }

            })
            .toList();
        AppendToStreamOptions appendOptions; 
        if (revision == null) {
            appendOptions = AppendToStreamOptions.get();
        } else {
            appendOptions = AppendToStreamOptions.get().streamState(StreamState.streamRevision(revision));
        }

        try {
            client.appendToStream(streamId.toString(), appendOptions, eventDatas.iterator()).get();
        } catch (ExecutionException | InterruptedException exception) {
            throw new RuntimeException("Failed to append events to stream: " + streamId, exception);
        }
    }

    public ReadResult readEvents(StreamId streamId) {
        ReadStreamOptions options = ReadStreamOptions.get()
            .forwards()
            .fromStart();

        try {
            ReadResult result = client.readStream(streamId.toString(), options).get();
            return result;
        } catch (ExecutionException | InterruptedException exception) {
            throw new RuntimeException("Failed to read events from stream: " + streamId, exception);
        }
    }

}
