package com.tompang.carpool.carpool_service.query.projector;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.tompang.carpool.carpool_service.common.DomainTopics;
import com.tompang.carpool.carpool_service.common.GeoUtils;
import com.tompang.carpool.carpool_service.query.entity.Carpool;
import com.tompang.carpool.carpool_service.query.geocode.GeocodeJobService;
import com.tompang.carpool.carpool_service.query.geocode.dto.ReverseGeocodeJobDto;
import com.tompang.carpool.carpool_service.query.geocode.dto.StaticMapJobDto;
import com.tompang.carpool.carpool_service.query.repository.CarpoolQueryRepository;
import com.tompang.carpool.event.carpool.CarpoolCreatedEvent;
import com.tompang.carpool.geospatial.enums.GeocodeEntity;
import com.tompang.carpool.geospatial.enums.GeocodeEntityField;

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
            .id(event.getCarpoolId())
            .totalSeats(event.getAvailableSeats())
            .driverId(event.getDriverId())
            .arrivalTime(event.getArrivalTime())
            .origin(GeoUtils.createPoint(event.getRoute().getOrigin()))
            .destination(GeoUtils.createPoint(event.getRoute().getDestination()))
            .build();

        repository.save(carpool);
        // create reverse geocode jobs for origin and destination.
        geocodeJobService.createReverseGeocodeJob(
            new ReverseGeocodeJobDto(
                event.getRoute().getOrigin().getLatitude(), 
                event.getRoute().getOrigin().getLongitude(), 
                GeocodeEntity.CARPOOL, carpool.getId(), GeocodeEntityField.ORIGIN
            )
        );
        geocodeJobService.createReverseGeocodeJob(
            new ReverseGeocodeJobDto(
                event.getRoute().getDestination().getLatitude(), 
                event.getRoute().getDestination().getLongitude(),
                GeocodeEntity.CARPOOL, carpool.getId(), GeocodeEntityField.DESTINATION
            )
        );
        // create static map jobs
        geocodeJobService.createStaticMapJob(
            StaticMapJobDto.builder()
                .latitude(event.getRoute().getOrigin().getLatitude())
                .longitude(event.getRoute().getOrigin().getLongitude())
                .entity(GeocodeEntity.CARPOOL)
                .entityId(carpool.getId())
                .field(GeocodeEntityField.ORIGIN)
                .build()
        );
        geocodeJobService.createStaticMapJob(
            StaticMapJobDto.builder()
                .latitude(event.getRoute().getDestination().getLatitude())
                .longitude(event.getRoute().getDestination().getLongitude())
                .entity(GeocodeEntity.CARPOOL)
                .entityId(carpool.getId())
                .field(GeocodeEntityField.DESTINATION)
                .build()
        );
    }
    
    // Delete
    public void handleDeleteCarpool() {

    }

}
