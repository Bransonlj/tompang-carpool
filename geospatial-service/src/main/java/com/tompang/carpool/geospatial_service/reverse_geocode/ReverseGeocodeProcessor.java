package com.tompang.carpool.geospatial_service.reverse_geocode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.tompang.carpool.geospatial_service.common.KafkaTopics;
import com.tompang.carpool.geospatial_service.common.exception.OnemapApiException;
import com.tompang.carpool.geospatial_service.config.RabbitConfig;
import com.tompang.carpool.geospatial_service.onemap.OnemapService;
import com.tompang.carpool.geospatial_service.onemap.dto.OnemapGeocodeResponseDto;
import com.tompang.carpool.geospatial_service.reverse_geocode.dto.ReverseGeocodeJobDto;
import com.tompang.carpool.geospatial_service.reverse_geocode.event.ReverseGeocodeCompletedEvent;


/**
 * Rabbitmq worker listening to the reverse-geocode queue, fetching geocode data from latlng using OneMap API.
 * Produces GeocodeReverseCompletedEvent kafka topic on completion
 */
@Component
public class ReverseGeocodeProcessor {

    private final Logger logger = LoggerFactory.getLogger(ReverseGeocodeProcessor.class);

    private final OnemapService onemapService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ReverseGeocodeProcessor(OnemapService onemapService, KafkaTemplate<String, Object> kafkaTemplate) {
        this.onemapService = onemapService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @RabbitListener(queues = RabbitConfig.REVERSE_GEOCODE_QUEUE)
    public void processReverseGeocodeJob(ReverseGeocodeJobDto job) {
        logger.info("Processing " + job.toString());

        ReverseGeocodeCompletedEvent event;
        try {
            OnemapGeocodeResponseDto response = onemapService.reverseGeocode(job.location.latitude, job.location.longitude);
            event = ReverseGeocodeCompletedEvent.from(job, response);
            logger.info("Processing completed" + event.toString());
        } catch (OnemapApiException e) {
            this.logger.error("Error processing", e);
            event = ReverseGeocodeCompletedEvent.from(job, null);
        }

        this.kafkaTemplate.send(KafkaTopics.Geocode.REVERSE_GEOCODE_COMPLETED, event);
    }
}
