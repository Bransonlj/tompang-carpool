package com.tompang.carpool.geospatial_service.reverse_geocode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.tompang.carpool.geospatial.ReverseGeocodeCompletedEvent;
import com.tompang.carpool.geospatial_service.common.KafkaTopics;
import com.tompang.carpool.geospatial_service.common.exception.OnemapApiException;
import com.tompang.carpool.geospatial_service.config.RabbitConfig;
import com.tompang.carpool.geospatial_service.onemap.OnemapService;
import com.tompang.carpool.geospatial_service.onemap.dto.OnemapGeocodeResponseDto;
import com.tompang.carpool.geospatial_service.reverse_geocode.dto.ReverseGeocodeJobDto;


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
            OnemapGeocodeResponseDto response = onemapService.reverseGeocode(job.latitude, job.longitude);
            if (response == null || response.getGeocodeInfo().isEmpty()) {
                // no response, fail
                throw new OnemapApiException("No response/empty response from Onemap.");
            }

            // we will use the first address (closest)
            OnemapGeocodeResponseDto.GeocodeInfo firstInfo = response.getGeocodeInfo().get(0);
            StringBuilder builder = new StringBuilder();

            if (!firstInfo.getBuildingName().equals(OnemapGeocodeResponseDto.NIL)) {
                builder.append(firstInfo.getBuildingName());
                builder.append(" ");
            }

            if (!firstInfo.getBlock().equals(OnemapGeocodeResponseDto.NIL)) {
                builder.append(firstInfo.getBlock());
                builder.append(" ");
            }

            if (!firstInfo.getRoad().equals(OnemapGeocodeResponseDto.NIL)) {
                builder.append(firstInfo.getRoad());
                builder.append(" ");
            }

            if (!firstInfo.getPostalCode().equals(OnemapGeocodeResponseDto.NIL)) {
                builder.append(firstInfo.getPostalCode());
                builder.append(" ");
            }

            if (builder.isEmpty()) {
                // no result found (NIL for all fields)
                throw new OnemapApiException("Address fields are NIL.");
            }

            // address successfully fetched.
            event = new ReverseGeocodeCompletedEvent(true, builder.toString(), job.entity, job.entityId, job.field);
            
            
            logger.info("Processing completed" + event.toString());
        } catch (OnemapApiException e) {
            this.logger.error("Error processing", e);
            event = new ReverseGeocodeCompletedEvent(false, null, job.entity, job.entityId, job.field);
        }

        this.kafkaTemplate.send(KafkaTopics.Geocode.REVERSE_GEOCODE_COMPLETED, event);
    }
}
