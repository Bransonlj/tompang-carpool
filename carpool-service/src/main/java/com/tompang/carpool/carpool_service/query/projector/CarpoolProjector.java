package com.tompang.carpool.carpool_service.query.projector;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolCreatedEvent;
import com.tompang.carpool.carpool_service.common.DomainTopics;
import com.tompang.carpool.carpool_service.query.entity.Carpool;
import com.tompang.carpool.carpool_service.query.repository.CarpoolRepository;

@Component
public class CarpoolProjector {

    private CarpoolRepository repository;

    public CarpoolProjector(CarpoolRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = DomainTopics.Carpool.CARPOOL_CREATED, groupId = "carpool-service-query")
    public void handleCarpoolCreated(CarpoolCreatedEvent event) {
        Carpool carpool = Carpool.builder()
            .id(event.getCarpoolId())
            .totalSeats(event.getAvailableSeats())
            .driverId(event.getDriverId())
            .arrivalTime(event.getArrivalTime()).origin(event.getOrigin())
            .destination(event.getDestination())
            .build();

        repository.save(carpool);
    }



    // Delete
    public void handleDeleteCarpool() {

    }

}
