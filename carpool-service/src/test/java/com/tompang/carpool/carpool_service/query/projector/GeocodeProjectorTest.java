package com.tompang.carpool.carpool_service.query.projector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tompang.carpool.carpool_service.query.entity.Carpool;
import com.tompang.carpool.carpool_service.query.entity.EventualAddress;
import com.tompang.carpool.carpool_service.query.entity.EventualAddressStatus;
import com.tompang.carpool.carpool_service.query.entity.RideRequest;
import com.tompang.carpool.carpool_service.query.repository.CarpoolQueryRepository;
import com.tompang.carpool.carpool_service.query.repository.RideRequestQueryRepository;
import com.tompang.carpool.geospatial.ReverseGeocodeCompletedEvent;
import com.tompang.carpool.geospatial.enums.GeocodeEntity;
import com.tompang.carpool.geospatial.enums.GeocodeEntityField;

public class GeocodeProjectorTest {
    @Mock
    private CarpoolQueryRepository carpoolRepository;

    @Mock
    private RideRequestQueryRepository rideRequestRepository;

    @InjectMocks
    private GeocodeProjector projector;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("tests for handleGeocodeReverseCompleted()")
    class HandleGeocodeReverseCompletedTests {

        @Test
        void shouldUpdateCarpool_whenSuccessCarpoolOrigin() {
            // assemble
            ReverseGeocodeCompletedEvent event = ReverseGeocodeCompletedEvent.newBuilder()
                    .setAddress("address")
                    .setEntity(GeocodeEntity.CARPOOL)
                    .setEntityId("carpool-1")
                    .setField(GeocodeEntityField.ORIGIN)
                    .setSuccess(true)
                    .build();
            Carpool carpool = new Carpool();
            when(carpoolRepository.findById(event.getEntityId())).thenReturn(Optional.of(carpool));

            // act
            projector.handleGeocodeReverseCompleted(event);

            // assert
            EventualAddress expectedAddress = EventualAddress.get();
            expectedAddress.setStatus(EventualAddressStatus.RESOLVED);
            expectedAddress.setAddressString(event.getAddress());
            assertEquals(expectedAddress, carpool.getOriginEventualAddress());
            assertEquals(EventualAddress.get(), carpool.getDestinationEventualAddress()); // not changed
            verify(carpoolRepository).save(carpool);
        }

        @Test
        void shouldUpdateCarpool_whenSuccessCarpoolDestination() {
            // assemble
            ReverseGeocodeCompletedEvent event = ReverseGeocodeCompletedEvent.newBuilder()
                    .setAddress("address")
                    .setEntity(GeocodeEntity.CARPOOL)
                    .setEntityId("carpool-1")
                    .setField(GeocodeEntityField.DESTINATION)
                    .setSuccess(true)
                    .build();
            Carpool carpool = new Carpool();
            when(carpoolRepository.findById(event.getEntityId())).thenReturn(Optional.of(carpool));

            // act
            projector.handleGeocodeReverseCompleted(event);

            // assert
            EventualAddress expectedAddress = EventualAddress.get();
            expectedAddress.setStatus(EventualAddressStatus.RESOLVED);
            expectedAddress.setAddressString(event.getAddress());
            assertEquals(expectedAddress, carpool.getDestinationEventualAddress());
            assertEquals(EventualAddress.get(), carpool.getOriginEventualAddress()); // not changed
            verify(carpoolRepository).save(carpool);
        }

        @Test
        void shouldUpdateCarpool_whenFailCarpoolOrigin() {
            // assemble
            ReverseGeocodeCompletedEvent event = ReverseGeocodeCompletedEvent.newBuilder()
                    .setEntity(GeocodeEntity.CARPOOL)
                    .setEntityId("carpool-1")
                    .setField(GeocodeEntityField.ORIGIN)
                    .setSuccess(false)
                    .build();
            Carpool carpool = new Carpool();
            when(carpoolRepository.findById(event.getEntityId())).thenReturn(Optional.of(carpool));

            // act
            projector.handleGeocodeReverseCompleted(event);

            // assert
            EventualAddress expectedAddress = EventualAddress.get();
            expectedAddress.setStatus(EventualAddressStatus.FAILED);
            assertEquals(expectedAddress, carpool.getOriginEventualAddress());
            assertEquals(EventualAddress.get(), carpool.getDestinationEventualAddress()); // not changed
            verify(carpoolRepository).save(carpool);
        }

        @Test
        void shouldUpdateCarpool_whenFailCarpoolDestination() {
            // assemble
            ReverseGeocodeCompletedEvent event = ReverseGeocodeCompletedEvent.newBuilder()
                    .setEntity(GeocodeEntity.CARPOOL)
                    .setEntityId("carpool-1")
                    .setField(GeocodeEntityField.DESTINATION)
                    .setSuccess(false)
                    .build();
            Carpool carpool = new Carpool();
            when(carpoolRepository.findById(event.getEntityId())).thenReturn(Optional.of(carpool));

            // act
            projector.handleGeocodeReverseCompleted(event);

            // assert
            EventualAddress expectedAddress = EventualAddress.get();
            expectedAddress.setStatus(EventualAddressStatus.FAILED);
            assertEquals(expectedAddress, carpool.getDestinationEventualAddress());
            assertEquals(EventualAddress.get(), carpool.getOriginEventualAddress()); // not changed
            verify(carpoolRepository).save(carpool);
        }

        @Test
        void shouldUpdateRideRequest_whenSuccessRideRequestOrigin() {
            // assemble
            ReverseGeocodeCompletedEvent event = ReverseGeocodeCompletedEvent.newBuilder()
                    .setAddress("address")
                    .setEntity(GeocodeEntity.RIDEREQUEST)
                    .setEntityId("request-1")
                    .setField(GeocodeEntityField.ORIGIN)
                    .setSuccess(true)
                    .build();
            RideRequest request = new RideRequest();
            when(rideRequestRepository.findById(event.getEntityId())).thenReturn(Optional.of(request));

            // act
            projector.handleGeocodeReverseCompleted(event);

            // assert
            EventualAddress expectedAddress = EventualAddress.get();
            expectedAddress.setStatus(EventualAddressStatus.RESOLVED);
            expectedAddress.setAddressString(event.getAddress());
            assertEquals(expectedAddress, request.getOriginEventualAddress());
            assertEquals(EventualAddress.get(), request.getDestinationEventualAddress()); // not changed
            verify(rideRequestRepository).save(request);
        }

        @Test
        void shouldUpdateRideRequest_whenSuccessRideRequestDestination() {
            // assemble
            ReverseGeocodeCompletedEvent event = ReverseGeocodeCompletedEvent.newBuilder()
                    .setAddress("address")
                    .setEntity(GeocodeEntity.RIDEREQUEST)
                    .setEntityId("request-1")
                    .setField(GeocodeEntityField.DESTINATION)
                    .setSuccess(true)
                    .build();
            RideRequest request = new RideRequest();
            when(rideRequestRepository.findById(event.getEntityId())).thenReturn(Optional.of(request));

            // act
            projector.handleGeocodeReverseCompleted(event);

            // assert
            EventualAddress expectedAddress = EventualAddress.get();
            expectedAddress.setStatus(EventualAddressStatus.RESOLVED);
            expectedAddress.setAddressString(event.getAddress());
            assertEquals(expectedAddress, request.getDestinationEventualAddress());
            assertEquals(EventualAddress.get(), request.getOriginEventualAddress()); // not changed
            verify(rideRequestRepository).save(request);
        }

        @Test
        void shouldUpdateRideRequest_whenFailRideRequestOrigin() {
            // assemble
            ReverseGeocodeCompletedEvent event = ReverseGeocodeCompletedEvent.newBuilder()
                    .setAddress("address")
                    .setEntity(GeocodeEntity.RIDEREQUEST)
                    .setEntityId("request-1")
                    .setField(GeocodeEntityField.ORIGIN)
                    .setSuccess(false)
                    .build();
            RideRequest request = new RideRequest();
            when(rideRequestRepository.findById(event.getEntityId())).thenReturn(Optional.of(request));

            // act
            projector.handleGeocodeReverseCompleted(event);

            // assert
            EventualAddress expectedAddress = EventualAddress.get();
            expectedAddress.setStatus(EventualAddressStatus.FAILED);
            assertEquals(expectedAddress, request.getOriginEventualAddress());
            assertEquals(EventualAddress.get(), request.getDestinationEventualAddress()); // not changed
            verify(rideRequestRepository).save(request);
        }

        @Test
        void shouldUpdateRideRequest_whenFailRideRequestDestination() {
            // assemble
            ReverseGeocodeCompletedEvent event = ReverseGeocodeCompletedEvent.newBuilder()
                    .setAddress("address")
                    .setEntity(GeocodeEntity.RIDEREQUEST)
                    .setEntityId("request-1")
                    .setField(GeocodeEntityField.DESTINATION)
                    .setSuccess(false)
                    .build();
            RideRequest request = new RideRequest();
            when(rideRequestRepository.findById(event.getEntityId())).thenReturn(Optional.of(request));

            // act
            projector.handleGeocodeReverseCompleted(event);

            // assert
            EventualAddress expectedAddress = EventualAddress.get();
            expectedAddress.setStatus(EventualAddressStatus.FAILED);
            assertEquals(expectedAddress, request.getDestinationEventualAddress());
            assertEquals(EventualAddress.get(), request.getOriginEventualAddress()); // not changed
            verify(rideRequestRepository).save(request);
        }
    }
}
