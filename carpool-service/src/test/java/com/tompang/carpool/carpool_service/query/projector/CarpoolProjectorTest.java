package com.tompang.carpool.carpool_service.query.projector;

import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tompang.carpool.carpool_service.common.GeoUtils;
import com.tompang.carpool.carpool_service.query.entity.Carpool;
import com.tompang.carpool.carpool_service.query.geocode.GeocodeJobService;
import com.tompang.carpool.carpool_service.query.geocode.dto.ReverseGeocodeJobDto;
import com.tompang.carpool.carpool_service.query.geocode.dto.StaticMapJobDto;
import com.tompang.carpool.carpool_service.query.repository.CarpoolQueryRepository;
import com.tompang.carpool.common.Location;
import com.tompang.carpool.common.Route;
import com.tompang.carpool.event.carpool.CarpoolCreatedEvent;
import com.tompang.carpool.geospatial.enums.GeocodeEntity;
import com.tompang.carpool.geospatial.enums.GeocodeEntityField;

public class CarpoolProjectorTest {
    @Mock
    private CarpoolQueryRepository repository;

    @Mock
    private GeocodeJobService geocodeJobService;

    @InjectMocks
    private CarpoolProjector projector;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private CarpoolCreatedEvent event;
    
    @BeforeEach
    void setup() {
        event = CarpoolCreatedEvent.newBuilder()
                .setCarpoolId("carpool-123")
                .setDriverId("driver-123")
                .setAvailableSeats(4)
                .setArrivalTime(Instant.now().truncatedTo(ChronoUnit.MILLIS))
                .setRoute(Route.newBuilder()
                        .setOrigin(new Location(1d, 2d))
                        .setDestination(new Location(3d, 4d))
                        .build())
                .build();
    }

    @Test
    void handleCarpoolCreated_createCarpoolRecordAndGeocodeJobs() {
        projector.handleCarpoolCreated(event);

        // assert
        verify(repository).save(Carpool.builder()
                .id(event.getCarpoolId())
                .totalSeats(event.getAvailableSeats())
                .driverId(event.getDriverId())
                .arrivalTime(event.getArrivalTime())
                .origin(GeoUtils.createPoint(event.getRoute().getOrigin()))
                .destination(GeoUtils.createPoint(event.getRoute().getDestination()))
                .build()
        );

        verify(geocodeJobService).createReverseGeocodeJob(ReverseGeocodeJobDto.builder()
                .latitude(event.getRoute().getOrigin().getLatitude())
                .longitude(event.getRoute().getOrigin().getLongitude())
                .entity(GeocodeEntity.CARPOOL)
                .entityId(event.getCarpoolId())
                .field(GeocodeEntityField.ORIGIN)
                .build());
        
        verify(geocodeJobService).createReverseGeocodeJob(ReverseGeocodeJobDto.builder()
                .latitude(event.getRoute().getDestination().getLatitude())
                .longitude(event.getRoute().getDestination().getLongitude())
                .entity(GeocodeEntity.CARPOOL)
                .entityId(event.getCarpoolId())
                .field(GeocodeEntityField.DESTINATION)
                .build());

        
        verify(geocodeJobService).createStaticMapJob(StaticMapJobDto.builder()
                .latitude(event.getRoute().getOrigin().getLatitude())
                .longitude(event.getRoute().getOrigin().getLongitude())
                .entity(GeocodeEntity.CARPOOL)
                .entityId(event.getCarpoolId())
                .field(GeocodeEntityField.ORIGIN)
                .build());
        
        verify(geocodeJobService).createStaticMapJob(StaticMapJobDto.builder()
                .latitude(event.getRoute().getDestination().getLatitude())
                .longitude(event.getRoute().getDestination().getLongitude())
                .entity(GeocodeEntity.CARPOOL)
                .entityId(event.getCarpoolId())
                .field(GeocodeEntityField.DESTINATION)
                .build());
    }
}
