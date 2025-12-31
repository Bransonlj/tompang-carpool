package com.tompang.carpool.carpool_service.command.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.tompang.carpool.carpool_service.command.domain.DomainEvent;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishDomainEvent(DomainEvent event) {
        kafkaTemplate.send(event.topicName(), event.getEvent()); // unwrap and publish the Avro schema event class
    }

    public void publishDomainEvent(DomainEvent event, String key) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(event.topicName(), key, event.getEvent()); // unwrap and publish the Avro schema event class
        future.whenComplete((result,ex)->{
            if (ex == null) {
                logger.info("Sent message=[" + result.getRecordMetadata().topic() + " - " + result.getRecordMetadata().timestamp() + ": " + key + "]");
            } else {
                logger.warn("Error sending message=[" +
                        result.getRecordMetadata().topic() + "] " + ex.getMessage());
            }
        });
    }

    public void publishDomainEvents(List<? extends DomainEvent> events) {
        for (DomainEvent event : events) {
            kafkaTemplate.send(event.topicName(), event.getEvent()); // unwrap and publish the Avro schema event class
        }
    }
}
