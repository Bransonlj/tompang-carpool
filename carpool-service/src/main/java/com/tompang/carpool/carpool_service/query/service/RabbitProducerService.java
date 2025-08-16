package com.tompang.carpool.carpool_service.query.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.tompang.carpool.carpool_service.config.RabbitConfig;
import com.tompang.carpool.carpool_service.query.dto.geocode.GeocodeReverseJobDto;

@Service
public class RabbitProducerService {
    private final RabbitTemplate rabbitTemplate;

    public RabbitProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendReverseGeocodeJob(GeocodeReverseJobDto data) {
        rabbitTemplate.convertAndSend(RabbitConfig.REVERSE_GEOCODE_QUEUE, data);
    }
}
