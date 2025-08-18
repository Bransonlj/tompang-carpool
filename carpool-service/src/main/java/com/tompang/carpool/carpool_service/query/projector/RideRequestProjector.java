package com.tompang.carpool.carpool_service.query.projector;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestCreatedEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestFailedEvent;
import com.tompang.carpool.carpool_service.common.DomainTopics;
import com.tompang.carpool.carpool_service.common.GeoUtils;
import com.tompang.carpool.carpool_service.query.entity.RideRequest;
import com.tompang.carpool.carpool_service.query.entity.RideRequestStatus;
import com.tompang.carpool.carpool_service.query.geocode.GeocodeJobService;
import com.tompang.carpool.carpool_service.query.geocode.dto.ReverseGeocodeJobDto;
import com.tompang.carpool.carpool_service.query.geocode.enums.GeocodeEntity;
import com.tompang.carpool.carpool_service.query.geocode.enums.GeocodeEntityField;
import com.tompang.carpool.carpool_service.query.repository.RideRequestQueryRepository;

@Component
public class RideRequestProjector {
    private final RideRequestQueryRepository repository;
    private final GeocodeJobService geocodeJobService;

    public RideRequestProjector(RideRequestQueryRepository repository, GeocodeJobService geocodeJobService) {
        this.repository = repository;
        this.geocodeJobService = geocodeJobService;
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
        geocodeJobService.createReverseGeocodeJob(new ReverseGeocodeJobDto(event.route.origin, GeocodeEntity.RIDEREQUEST, request.getId(), GeocodeEntityField.ORIGIN));
        geocodeJobService.createReverseGeocodeJob(new ReverseGeocodeJobDto(event.route.destination, GeocodeEntity.RIDEREQUEST, request.getId(), GeocodeEntityField.DESTINATION));
    }

    @KafkaListener(topics = DomainTopics.RideRequest.REQUEST_FAILED, groupId = "carpool-service-query")
    public void handleRideRequestFailed(RideRequestFailedEvent event) {
        RideRequest request = repository.findById(event.requestId).orElseThrow();
        request.setStatus(RideRequestStatus.FAILED);
        repository.save(request);
    }

}
