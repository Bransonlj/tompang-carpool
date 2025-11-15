package com.tompang.carpool.geospatial_service.processor.static_map;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.tompang.carpool.geospatial.StaticMapCompletedEvent;
import com.tompang.carpool.geospatial.enums.GeocodeEntity;
import com.tompang.carpool.geospatial.enums.GeocodeEntityField;
import com.tompang.carpool.geospatial_service.common.KafkaTopics;
import com.tompang.carpool.geospatial_service.common.exception.OnemapApiException;
import com.tompang.carpool.geospatial_service.config.RabbitConfig;
import com.tompang.carpool.geospatial_service.onemap.OnemapService;
import com.tompang.carpool.geospatial_service.processor.static_map.dto.StaticMapJobDto;
import com.tompang.carpool.geospatial_service.service.S3Service;
import com.tompang.carpool.geospatial_service.service.S3Service.Directory;

@Component
public class StaticMapProcessor {
    private final Logger logger = LoggerFactory.getLogger(StaticMapProcessor.class);

    private final OnemapService onemapService;
    private final S3Service s3Service;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public StaticMapProcessor(OnemapService onemapService, S3Service s3Service, KafkaTemplate<String, Object> kafkaTemplate) {
        this.onemapService = onemapService;
        this.s3Service = s3Service;
        this.kafkaTemplate = kafkaTemplate;
    }

    @RabbitListener(queues = RabbitConfig.STATIC_MAP_QUEUE)
    public void processStaticMapJob(StaticMapJobDto job) {
        logger.info("Processing " + job.toString());
        StaticMapCompletedEvent event;
        try {
            byte[] imageBytes = onemapService.staticImage(job.latitude, job.longitude);
            Directory directory;
            if (job.entity.equals(GeocodeEntity.CARPOOL)) {
                if (job.field.equals(GeocodeEntityField.ORIGIN)) {
                    directory = Directory.CARPOOL_ORIGIN_MAP;
                } else {
                    directory = Directory.CARPOOL_DESTINATION_MAP;
                }
            } else {
                if (job.field.equals(GeocodeEntityField.ORIGIN)) {
                    directory = Directory.RIDE_REQUEST_ORIGIN_MAP;
                } else {
                    directory = Directory.RIDE_REQUEST_DESTINATION_MAP;
                }
            }
            String imageKey = s3Service.uploadFile(job.entityId, directory, imageBytes);
            event = new StaticMapCompletedEvent(true, imageKey, job.entity, job.entityId, job.field);
            logger.info("Processing completed" + event.toString());
        } catch (OnemapApiException | IOException e) {
            this.logger.error("Error processing", e.getMessage());
            event = new StaticMapCompletedEvent(false, null, job.entity, job.entityId, job.field);
        }

        this.kafkaTemplate.send(KafkaTopics.Geocode.STATIC_MAP_COMPLETED, event);
    }
}
