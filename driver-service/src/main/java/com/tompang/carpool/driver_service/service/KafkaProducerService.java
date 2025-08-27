package com.tompang.carpool.driver_service.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.tompang.carpool.driver_service.common.KafkaTopics;
import com.tompang.carpool.event.driver.DriverRegistrationFailedEvent;
import com.tompang.carpool.event.driver.DriverRegistrationSucceededEvent;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void produceDriverRegistrationSucceeded(String driverId, String userId) {
        kafkaTemplate.send(
            KafkaTopics.Driver.DRIVER_REGISTRATION_SUCCEEDED, 
            new DriverRegistrationSucceededEvent(driverId, userId));
    }

    public void produceDriverRegistrationFailed(String driverId, String userId) {
        kafkaTemplate.send(
            KafkaTopics.Driver.DRIVER_REGISTRATION_FAILED, 
            new DriverRegistrationFailedEvent(driverId, userId));
    }
}
