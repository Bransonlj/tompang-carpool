package com.tompang.carpool.carpool_service.query.projector;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestCreatedEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestFailedEvent;
import com.tompang.carpool.carpool_service.common.DomainTopics;
import com.tompang.carpool.carpool_service.common.GeoUtils;
import com.tompang.carpool.carpool_service.query.dto.geocode.GeocodeReverseJobDto;
import com.tompang.carpool.carpool_service.query.dto.geocode.enums.GeocodeEntity;
import com.tompang.carpool.carpool_service.query.dto.geocode.enums.GeocodeEntityField;
import com.tompang.carpool.carpool_service.query.entity.RideRequest;
import com.tompang.carpool.carpool_service.query.entity.RideRequestStatus;
import com.tompang.carpool.carpool_service.query.repository.RideRequestQueryRepository;
import com.tompang.carpool.carpool_service.query.service.RabbitProducerService;

@Component
public class RideRequestProjector {
    private final RideRequestQueryRepository repository;
    private final RabbitProducerService rabbitProducerService;

    public RideRequestProjector(RideRequestQueryRepository repository, RabbitProducerService rabbitProducerService) {
        this.repository = repository;
        this.rabbitProducerService = rabbitProducerService;
    }

    @KafkaListener(topics = DomainTopics.RideRequest.REQUEST_CREATED, groupId = "carpool-service-query")
    public void handleRideRequestCreated(RideRequestCreatedEvent event) {
        RideRequest request = RideRequest.builder()
            .id(event.requestId)
            .riderId(event.riderId)
            .passengers(event.passengers)
            .startTime(event.startTime)
            .endTime(event.endTime)
            .origin(GeoUtils.createPoint(event.route.origin))
            .destination(GeoUtils.createPoint(event.route.destination))
            .build();
        
        repository.save(request);
        // create reverse geocode jobs for origin and destination.
        rabbitProducerService.sendReverseGeocodeJob(new GeocodeReverseJobDto(event.route.origin, GeocodeEntity.RIDEREQUEST, request.getId(), GeocodeEntityField.ORIGIN));
        rabbitProducerService.sendReverseGeocodeJob(new GeocodeReverseJobDto(event.route.destination, GeocodeEntity.RIDEREQUEST, request.getId(), GeocodeEntityField.DESTINATION));
    }

    @KafkaListener(topics = DomainTopics.RideRequest.REQUEST_FAILED, groupId = "carpool-service-query")
    public void handleRideRequestFailed(RideRequestFailedEvent event) {
        RideRequest request = repository.findById(event.requestId).orElseThrow();
        request.setStatus(RideRequestStatus.FAILED);
        repository.save(request);
    }

}
