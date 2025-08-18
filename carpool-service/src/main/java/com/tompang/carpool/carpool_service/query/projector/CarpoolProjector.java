package com.tompang.carpool.carpool_service.query.projector;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolCreatedEvent;
import com.tompang.carpool.carpool_service.common.DomainTopics;
import com.tompang.carpool.carpool_service.common.GeoUtils;
import com.tompang.carpool.carpool_service.query.entity.Carpool;
import com.tompang.carpool.carpool_service.query.geocode.GeocodeJobService;
import com.tompang.carpool.carpool_service.query.geocode.dto.ReverseGeocodeJobDto;
import com.tompang.carpool.carpool_service.query.geocode.enums.GeocodeEntity;
import com.tompang.carpool.carpool_service.query.geocode.enums.GeocodeEntityField;
import com.tompang.carpool.carpool_service.query.repository.CarpoolQueryRepository;

@Component
public class CarpoolProjector {

    private final CarpoolQueryRepository repository;
    private final GeocodeJobService geocodeJobService;

    public CarpoolProjector(CarpoolQueryRepository repository, GeocodeJobService geocodeJobService) {
        this.repository = repository;
        this.geocodeJobService = geocodeJobService;
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
        geocodeJobService.createReverseGeocodeJob(new ReverseGeocodeJobDto(event.route.origin, GeocodeEntity.CARPOOL, carpool.getId(), GeocodeEntityField.ORIGIN));
        geocodeJobService.createReverseGeocodeJob(new ReverseGeocodeJobDto(event.route.destination, GeocodeEntity.CARPOOL, carpool.getId(), GeocodeEntityField.DESTINATION));
    }
    
    // Delete
    public void handleDeleteCarpool() {

    }

}
