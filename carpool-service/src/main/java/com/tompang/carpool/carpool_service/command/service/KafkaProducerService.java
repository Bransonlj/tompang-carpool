package com.tompang.carpool.carpool_service.command.service;

import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.tompang.carpool.carpool_service.command.domain.DomainEvent;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishDomainEvent(DomainEvent event) {
        kafkaTemplate.send(event.topicName(), event.getEvent()); // unwrap and publish the Avro schema event class
    }

    public void publishDomainEvents(List<? extends DomainEvent> events) {
        for (DomainEvent event : events) {
            kafkaTemplate.send(event.topicName(), event.getEvent()); // unwrap and publish the Avro schema event class
        }
    }
}
