package com.tompang.carpool.carpool_service.query.geocode;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.tompang.carpool.carpool_service.config.RabbitConfig;
import com.tompang.carpool.carpool_service.query.geocode.dto.ReverseGeocodeJobDto;
import com.tompang.carpool.carpool_service.query.geocode.dto.StaticMapJobDto;

@Service
public class GeocodeJobService {
    private final RabbitTemplate rabbitTemplate;

    public GeocodeJobService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void createReverseGeocodeJob(ReverseGeocodeJobDto data) {
        rabbitTemplate.convertAndSend(RabbitConfig.REVERSE_GEOCODE_QUEUE, data);
    }

    public void createStaticMapJob(StaticMapJobDto data) {
        rabbitTemplate.convertAndSend(RabbitConfig.STATIC_MAP_QUEUE, data);
    }
}
