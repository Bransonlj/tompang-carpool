package com.tompang.carpool.carpool_service.command.repository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonDecoder;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tompang.carpool.carpool_service.command.domain.DomainEvent;
import com.tompang.carpool.carpool_service.command.domain.DomainEventRegistry;
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
    }

    public static final class RideRequestConstants {
        public static final String STREAM_PREFIX = "ride-request";
    }

    private final Logger logger = LoggerFactory.getLogger(EventRepository.class);
    private final KurrentDBClient client;

    public EventRepository(KurrentDBClient client) {
        this.client = client;
    }

    /**
     * Serializes Avro schema record to JSON format.
     * @param <T>
     * @param record
     * @return
     */
    private <T extends SpecificRecord> byte[] serializeAvro(T record) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            SpecificDatumWriter<T> writer = new SpecificDatumWriter<>(record.getSchema());
            Encoder encoder = EncoderFactory.get().jsonEncoder(record.getSchema(), out);
            writer.write(record, encoder);
            encoder.flush();
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize Avro record as JSON: " + record.getClass(), e);
        }
    }

    /**
     * Deserializes JSON formatted data to the specified Avro Schema class
     * @param <T>
     * @param data
     * @param clazz
     * @return
     */
    private <T extends SpecificRecord> T deserializeAvro(byte[] data, Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            SpecificDatumReader<T> reader = new SpecificDatumReader<>(instance.getSchema());
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            JsonDecoder decoder = DecoderFactory.get().jsonDecoder(instance.getSchema(), in);
            return reader.read(instance, decoder);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize Avro JSON record: " + clazz.getName(), e);
        }
    }

    public <T extends DomainEvent> T deserializeEvent(ResolvedEvent resolvedEvent) {
        RecordedEvent recordedEvent = resolvedEvent.getOriginalEvent();
        String eventType = recordedEvent.getEventType();
        byte[] data = recordedEvent.getEventData();
        try {
            Class<?> clazz = Class.forName(eventType);
            if (!SpecificRecord.class.isAssignableFrom(clazz)) {
                throw new RuntimeException("Event type is not Avro SpecificRecord: " + eventType);
            }

            @SuppressWarnings("unchecked")
            SpecificRecord record = deserializeAvro(data, (Class<? extends SpecificRecord>) clazz);
            return DomainEventRegistry.wrap(record);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Event class not found: " + eventType, e);
        }
    }

    public <T extends DomainEvent> List<T> deserializeEvents(List<ResolvedEvent> resolvedEvents) {
        List<T> domainEvents = new ArrayList<>();
        for (ResolvedEvent resolvedEvent : resolvedEvents) {
            try {
                domainEvents.add(deserializeEvent(resolvedEvent));
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
        // Serialize events
        List<EventData> eventDatas = events.stream()
            .map(event -> {
                Object raw = event.getEvent();
                if (!(raw instanceof SpecificRecord record)) {
                    throw new RuntimeException("DomainEvent payload is not an Avro SpecificRecord: " + raw.getClass());
                }
                byte[] avroBytes = serializeAvro(record);
                return EventData
                    .builderAsJson(
                        UUID.randomUUID(), 
                        event.getEvent().getClass().getName(),
                        avroBytes
                    ).build();
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
