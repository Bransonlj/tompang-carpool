package com.tompang.carpool.carpool_service.query.projector;

import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tompang.carpool.carpool_service.common.GeoUtils;
import com.tompang.carpool.carpool_service.query.entity.RideRequest;
import com.tompang.carpool.carpool_service.query.geocode.GeocodeJobService;
import com.tompang.carpool.carpool_service.query.geocode.dto.ReverseGeocodeJobDto;
import com.tompang.carpool.carpool_service.query.repository.RideRequestQueryRepository;
import com.tompang.carpool.common.Location;
import com.tompang.carpool.common.Route;
import com.tompang.carpool.event.ride_request.RideRequestCreatedEvent;
import com.tompang.carpool.geospatial.enums.GeocodeEntity;
import com.tompang.carpool.geospatial.enums.GeocodeEntityField;

public class RideRequestProjectorTest {

    @Mock
    private RideRequestQueryRepository rideRequestRepository;

    @Mock
    private GeocodeJobService geocodeJobService;

    @InjectMocks
    private RideRequestProjector projector;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("tests for handleRideRequestCreated()")
    class HandleRideRequestCreatedTests {
        private RideRequestCreatedEvent event;
        
        @BeforeEach
        void setup() {
            event = RideRequestCreatedEvent.newBuilder()
                    .setRequestId("request-1")
                    .setRiderId("rider-1")
                    .setPassengers(2)
                    .setStartTime(LocalDateTime.now())
                    .setEndTime(LocalDateTime.now().plusHours(1))
                    .setRoute(Route.newBuilder()
                            .setOrigin(Location.newBuilder()
                                    .setLatitude(2)
                                    .setLongitude(4)
                                    .build())
                            .setDestination(Location.newBuilder()
                                    .setLatitude(1)
                                    .setLongitude(3)
                                    .build())
                            .build())
                    .build();
        }

        
        @Test
        void shouldCreateAndSaveRideRequestAndCreateReverseGeocodeJob() {
            // act
            projector.handleRideRequestCreated(event);

            // assert
            verify(rideRequestRepository).save(RideRequest.builder()
                    .id("request-1")
                    .riderId("rider-1")
                    .passengers(2)
                    .startTime(LocalDateTime.now())
                    .endTime(LocalDateTime.now().plusHours(1))
                    .origin(GeoUtils.createPoint(new Location(2d, 4d)))
                    .destination(GeoUtils.createPoint(new Location(1d, 3d)))
                    .build()
            );

            verify(geocodeJobService).createReverseGeocodeJob(ReverseGeocodeJobDto.builder()
                    .latitude(2)
                    .longitude(4)
                    .entity(GeocodeEntity.RIDEREQUEST)
                    .entityId("request-1")
                    .field(GeocodeEntityField.ORIGIN)
                    .build());
            
            verify(geocodeJobService).createReverseGeocodeJob(ReverseGeocodeJobDto.builder()
                    .latitude(1)
                    .longitude(3)
                    .entity(GeocodeEntity.RIDEREQUEST)
                    .entityId("request-1")
                    .field(GeocodeEntityField.DESTINATION)
                    .build());

        }
    }
}
