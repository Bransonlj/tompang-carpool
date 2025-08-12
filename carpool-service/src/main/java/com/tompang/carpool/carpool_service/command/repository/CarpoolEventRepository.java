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
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolCreatedEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolEvent;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolMatchedEvent;

import io.kurrent.dbclient.EventData;
import io.kurrent.dbclient.KurrentDBClient;
import io.kurrent.dbclient.ReadResult;
import io.kurrent.dbclient.ReadStreamOptions;
import io.kurrent.dbclient.RecordedEvent;
import io.kurrent.dbclient.ResolvedEvent;

@Repository
public class CarpoolEventRepository {

    private static final String STREAM_PREFIX = "carpool";
    private static final Map<String, Class<? extends CarpoolEvent>> EVENT_TYPE_MAP = Map.of(
        CarpoolCreatedEvent.class.getName(), CarpoolCreatedEvent.class,
        CarpoolMatchedEvent.class.getName(), CarpoolMatchedEvent.class
    );

    private final KurrentDBClient client;
    private final ObjectMapper objectMapper;

    public CarpoolEventRepository(KurrentDBClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    private static String getStreamId(String carpoolId) {
        return STREAM_PREFIX + "_" + carpoolId;
    }

    public void appendEvents(String carpoolId, List<CarpoolEvent> events) {
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
            
        client.appendToStream(getStreamId(carpoolId), eventDatas.iterator());
    }

    public List<CarpoolEvent> readEvents(String carpoolId) {
        List<CarpoolEvent> events = new ArrayList<>();

        ReadStreamOptions options = ReadStreamOptions.get()
            .forwards()
            .fromStart();

        try {
            ReadResult result = client.readStream(getStreamId(carpoolId), options)
                .get();

            for (ResolvedEvent resolvedEvent : result.getEvents()) {
                RecordedEvent recordedEvent = resolvedEvent.getOriginalEvent();

                String eventType = recordedEvent.getEventType();
                byte[] json = recordedEvent.getEventData();

                Class<? extends CarpoolEvent> clazz = EVENT_TYPE_MAP.get(eventType);

                if (clazz == null) {
                    System.err.println("Unknown event type: " + eventType);
                    continue;
                }

                try {
                    CarpoolEvent eventObj = objectMapper.readValue(json, clazz);
                    events.add(eventObj);
                } catch (IOException e) {
                    System.err.printf("Failed to deserialize event type '%s': %s%n", eventType, e.getMessage());
                }
            }

            return events;
        } catch (ExecutionException | InterruptedException exception) {
            throw new RuntimeException("Failed to read events from stream: " + getStreamId(carpoolId), exception);
        }
    }
}
