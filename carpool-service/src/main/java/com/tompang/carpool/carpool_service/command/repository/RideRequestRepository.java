package com.tompang.carpool.carpool_service.command.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestCreatedEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestMatchedEvent;

import io.kurrent.dbclient.EventData;
import io.kurrent.dbclient.KurrentDBClient;
import io.kurrent.dbclient.ReadResult;
import io.kurrent.dbclient.ReadStreamOptions;
import io.kurrent.dbclient.RecordedEvent;
import io.kurrent.dbclient.ResolvedEvent;

@Repository
public class RideRequestRepository {

    private final KurrentDBClient client;
    private ObjectMapper objectMapper;

    private static final String STREAM_PREFIX = "ride-request";
    private static final Map<String, Class<? extends RideRequestEvent>> EVENT_TYPE_MAP = Map.of(
        RideRequestCreatedEvent.class.getName(), RideRequestCreatedEvent.class,
        RideRequestMatchedEvent.class.getName(), RideRequestMatchedEvent.class
    );

    public RideRequestRepository(KurrentDBClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    private static String getStreamId(String requestId) {
        return STREAM_PREFIX + "_" + requestId;
    }

    public void appendEvents(String requestId, List<RideRequestEvent> events) {
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
            
        client.appendToStream(getStreamId(requestId), eventDatas.iterator());
    }

    public List<RideRequestEvent> readEvents(String requestId) {
        List<RideRequestEvent> events = new ArrayList<>();

        ReadStreamOptions options = ReadStreamOptions.get()
            .forwards()
            .fromStart();

        try {
            ReadResult result = client.readStream(getStreamId(requestId), options)
                .get();

            for (ResolvedEvent resolvedEvent : result.getEvents()) {
                RecordedEvent recordedEvent = resolvedEvent.getOriginalEvent();

                String eventType = recordedEvent.getEventType();
                byte[] json = recordedEvent.getEventData();

                Class<? extends RideRequestEvent> clazz = EVENT_TYPE_MAP.get(eventType);

                if (clazz == null) {
                    System.err.println("Unknown event type: " + eventType);
                    continue;
                }

                try {
                    RideRequestEvent eventObj = objectMapper.readValue(json, clazz);
                    events.add(eventObj);
                } catch (IOException e) {
                    System.err.printf("Failed to deserialize event type '%s': %s%n", eventType, e.getMessage());
                }
            }

            return events;
        } catch (ExecutionException | InterruptedException exception) {
            throw new RuntimeException("Failed to read events from stream: " + getStreamId(requestId), exception);
        }
    }
}
