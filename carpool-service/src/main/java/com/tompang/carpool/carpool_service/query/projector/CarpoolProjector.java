package com.tompang.carpool.carpool_service.query.projector;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolCreatedEvent;
import com.tompang.carpool.carpool_service.common.DomainTopics;
import com.tompang.carpool.carpool_service.common.GeoUtils;
import com.tompang.carpool.carpool_service.query.dto.geocode.GeocodeReverseJobDto;
import com.tompang.carpool.carpool_service.query.dto.geocode.enums.GeocodeEntity;
import com.tompang.carpool.carpool_service.query.dto.geocode.enums.GeocodeEntityField;
import com.tompang.carpool.carpool_service.query.entity.Carpool;
import com.tompang.carpool.carpool_service.query.repository.CarpoolQueryRepository;
import com.tompang.carpool.carpool_service.query.service.RabbitProducerService;

@Component
public class CarpoolProjector {

    private final CarpoolQueryRepository repository;
    private final RabbitProducerService rabbitProducerService;

    public CarpoolProjector(CarpoolQueryRepository repository, RabbitProducerService rabbitProducerService) {
        this.repository = repository;
        this.rabbitProducerService = rabbitProducerService;
    }

    @KafkaListener(topics = DomainTopics.Carpool.CARPOOL_CREATED, groupId = "carpool-service-query")
    public void handleCarpoolCreated(CarpoolCreatedEvent event) {
        Carpool carpool = Carpool.builder()
            .id(event.carpoolId)
            .totalSeats(event.availableSeats)
            .driverId(event.driverId)
            .arrivalTime(event.arrivalTime)
            .origin(GeoUtils.createPoint(event.route.origin))
            .destination(GeoUtils.createPoint(event.route.destination))
            .build();

        repository.save(carpool);
        // create reverse geocode jobs for origin and destination.
        rabbitProducerService.sendReverseGeocodeJob(new GeocodeReverseJobDto(event.route.origin, GeocodeEntity.CARPOOL, carpool.getId(), GeocodeEntityField.ORIGIN));
        rabbitProducerService.sendReverseGeocodeJob(new GeocodeReverseJobDto(event.route.destination, GeocodeEntity.CARPOOL, carpool.getId(), GeocodeEntityField.DESTINATION));
    }
    
    // Delete
    public void handleDeleteCarpool() {

    }

}
