package com.tompang.carpool.carpool_service.query.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.tompang.carpool.carpool_service.common.DomainTopics;
import com.tompang.carpool.carpool_service.common.exceptions.OnemapApiException;
import com.tompang.carpool.carpool_service.config.RabbitConfig;
import com.tompang.carpool.carpool_service.query.dto.geocode.GeocodeResponse;
import com.tompang.carpool.carpool_service.query.dto.geocode.GeocodeReverseCompletedEvent;
import com.tompang.carpool.carpool_service.query.dto.geocode.GeocodeReverseJobDto;
import com.tompang.carpool.carpool_service.query.service.OnemapService;

/**
 * Rabbitmq worker listening to the reverse-geocode queue, fetching geocode data from latlong using OneMap API.
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
    public void processReverseGeocodeJob(GeocodeReverseJobDto job) {
        logger.info("Processing " + job.toString());

        GeocodeReverseCompletedEvent event;
        try {
            GeocodeResponse response = onemapService.reverseGeocode(job.location);
            event = GeocodeReverseCompletedEvent.from(job, response);
            logger.info("Processing completed" + event.toString());
        } catch (OnemapApiException e) {
            this.logger.error("Error processing", e);
            event = GeocodeReverseCompletedEvent.from(job, null);
        }

        this.kafkaTemplate.send(DomainTopics.Geocode.GEOCODE_REVERSE_COMPLETED, event);
    }
}
