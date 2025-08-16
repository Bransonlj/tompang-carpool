package com.tompang.carpool.carpool_service.query.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.tompang.carpool.carpool_service.common.DomainTopics;
import com.tompang.carpool.carpool_service.config.RabbitConfig;
import com.tompang.carpool.carpool_service.query.dto.geocode.GeocodeResponse;
import com.tompang.carpool.carpool_service.query.dto.geocode.GeocodeReverseCompletedEvent;
import com.tompang.carpool.carpool_service.query.dto.geocode.GeocodeReverseJobDto;

/**
 * Rabbitmq worker listening to the reverse-geocode queue, fetching geocode data from latlong using OneMap API.
 * Produces GeocodeReverseCompletedEvent kafka topic on completion
 */
@Component
public class ReverseGeocodeProcessor {

    private final Logger logger = LoggerFactory.getLogger(ReverseGeocodeProcessor.class);

    private final RestClient restClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ReverseGeocodeProcessor(RestClient.Builder builder, KafkaTemplate<String, Object> kafkaTemplate) {
        this.restClient = builder
                .baseUrl("https://www.onemap.gov.sg")
                .build();
        this.kafkaTemplate = kafkaTemplate;
    }

    @RabbitListener(queues = RabbitConfig.REVERSE_GEOCODE_QUEUE)
    public void processReverseGeocodeJob(GeocodeReverseJobDto job) {
        logger.info("Processing " + job.toString());

        GeocodeReverseCompletedEvent event;
        try {
            GeocodeResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/public/revgeocode")
                            .queryParam("location", job.location.latitude + "," + job.location.longitude)
                            .queryParam("buffer", 50) // use a buffer of 50
                            .queryParam("addressType", "All")
                            .queryParam("otherFeatures", "N")
                            .build())
                    // TODO store key in redis, use auth endpoint to generate new keys
                    .header("Authorization", "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjo4MzA1LCJmb3JldmVyIjpmYWxzZSwiaXNzIjoiT25lTWFwIiwiaWF0IjoxNzU1MjY5Mjk1LCJuYmYiOjE3NTUyNjkyOTUsImV4cCI6MTc1NTUyODQ5NSwianRpIjoiMWQ0MGNhZjYtZmQwNS00NmZkLTkxYzAtZWFiMTg1YjQxYTAxIn0.mz7Y08x2OTUVzwd8jr39GVMDXbaf-4_ehepH9lLNOY76jV8E9CE7nKuCK0XMqvmi26y-0PA78m2oT3Mynp204u-Vm7KUJDn1C6PxeF8ctmKO7G0TDEFBP3KI70jAss9KzxL2cfTMqDKVZNK7eDLKIX3B85FRIE8HOz-qSCJW7dljn12NOyquD1I_ik79ky8voJEk-yFL3hTB-QrNatwuoRVFU6eMEqOLccnYzbbOXiNi5JfBnDKW6Ka04zcaJxyniBeFN6CscKAKsF2p5nDt3_vs87GZHWnFhTm6qgar-RwIAPwBsxVrVEwCG9rnCtY9l8DWd0kGcu2TqwL2bC7djQ")
                    .retrieve()
                    .body(GeocodeResponse.class);

            event = GeocodeReverseCompletedEvent.from(job, response);
            logger.info("Processing completed" + event.toString());
        } catch (HttpClientErrorException.TooManyRequests e) {
            // 429 error
            // TODO retry on 429
            logger.error("Processing client error: " + e.getStatusCode() + " -> " + e.getResponseBodyAsString());
            event = GeocodeReverseCompletedEvent.from(job, null);
        } catch (HttpClientErrorException e) {
            // 4xx errors
            logger.error("Processing client error: " + e.getStatusCode() + " -> " + e.getResponseBodyAsString());
            event = GeocodeReverseCompletedEvent.from(job, null);
        } catch (HttpServerErrorException e) {
            // 5xx errors
            logger.error("Processing server error: " + e.getStatusCode() + " -> " + e.getResponseBodyAsString());
            event = GeocodeReverseCompletedEvent.from(job, null);
        } catch (RestClientResponseException e) {
            // Any other unexpected status code
            logger.error("Processing other HTTP error: " + e.getStatusCode());
            event = GeocodeReverseCompletedEvent.from(job, null);
        }

        this.kafkaTemplate.send(DomainTopics.Geocode.GEOCODE_REVERSE_COMPLETED, event);
    }
}
