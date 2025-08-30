package com.tompang.carpool.driver_service.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.tompang.carpool.driver_service.common.KafkaTopics;
import com.tompang.carpool.event.driver.DriverRegistrationApprovedEvent;
import com.tompang.carpool.event.driver.DriverRegistrationRejectedEvent;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void produceDriverRegistrationApproved(String driverId, String userId) {
        kafkaTemplate.send(
            KafkaTopics.Driver.DRIVER_REGISTRATION_APPROVED, 
            new DriverRegistrationApprovedEvent(driverId, userId));
    }

    public void produceDriverRegistrationRejected(String driverId, String userId) {
        kafkaTemplate.send(
            KafkaTopics.Driver.DRIVER_REGISTRATION_REJECTED, 
            new DriverRegistrationRejectedEvent(driverId, userId));
    }
}
