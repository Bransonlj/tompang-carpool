package com.tompang.carpool.carpool_service.query.projector;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestCreatedEvent;
import com.tompang.carpool.carpool_service.common.DomainTopics;
import com.tompang.carpool.carpool_service.query.entity.RideRequest;
import com.tompang.carpool.carpool_service.query.repository.RideRequestQueryRepository;

@Component
public class RideRequestProjector {
    private final RideRequestQueryRepository repository;

    public RideRequestProjector(RideRequestQueryRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = DomainTopics.RideRequest.REQUEST_CREATED, groupId = "carpool-service-query")
    public void handleRideRequestCreated(RideRequestCreatedEvent event) {
        RideRequest request = RideRequest.builder()
            .id(event.getRequestId())
            .riderId(event.getRiderId())
            .passengers(event.getPassengers())
            .startTime(event.getStartTime())
            .endTime(event.getEndTime())
            .origin(event.getOrigin())
            .destination(event.getDestination())
            .build();
        
        repository.save(request);
    }

}
