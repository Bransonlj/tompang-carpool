package com.tompang.carpool.carpool_service.query.projector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tompang.carpool.carpool_service.common.exceptions.BadRequestException;
import com.tompang.carpool.carpool_service.query.entity.Carpool;
import com.tompang.carpool.carpool_service.query.entity.RideRequest;
import com.tompang.carpool.carpool_service.query.entity.RideRequestStatus;
import com.tompang.carpool.carpool_service.query.repository.CarpoolQueryRepository;
import com.tompang.carpool.carpool_service.query.repository.RideRequestQueryRepository;
import com.tompang.carpool.event.carpool.CarpoolMatchedEvent;
import com.tompang.carpool.event.carpool.CarpoolRequestInvalidatedEvent;
import com.tompang.carpool.event.ride_request.RideRequestAcceptedEvent;
import com.tompang.carpool.event.ride_request.RideRequestDeclinedEvent;
import com.tompang.carpool.event.ride_request.RideRequestMatchedEvent;

public class MatchProjectorTest {

    @Mock
    private CarpoolQueryRepository carpoolRepository;

    @Mock
    private RideRequestQueryRepository rideRequestRepository;

    @InjectMocks
    private MatchProjector projector;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("tests for handleCarpoolMatched()")
    class HandleCarpoolMatchedTests {

        private CarpoolMatchedEvent event;

        @BeforeEach
        void setup() {
            event = CarpoolMatchedEvent.newBuilder()
                    .setCarpoolId("carpool-1")
                    .setRideRequestId("request-1")
                    .setDriverId("driver-1")
                    .build();
        }

        @Test
        void shouldMatchCarpoolWithRequest_whenBothExist() {
            // Arrange
            Carpool carpool = new Carpool();
            RideRequest rideRequest = new RideRequest();

            when(carpoolRepository.findById(event.getCarpoolId())).thenReturn(Optional.of(carpool));
            when(rideRequestRepository.findById(event.getRideRequestId())).thenReturn(Optional.of(rideRequest));

            // Act
            projector.handleCarpoolMatched(event);

            // Assert
            assertTrue(carpool.getPendingRideRequests().contains(rideRequest));
            assertTrue(rideRequest.getMatchedCarpools().contains(carpool));

            verify(carpoolRepository).save(carpool);
            verify(rideRequestRepository).save(rideRequest);
        }

        
        @Test
        void shouldDoNothing_whenCarpoolNotFound() {
            // Arrange
            when(carpoolRepository.findById(event.getCarpoolId())).thenReturn(Optional.empty());
            when(rideRequestRepository.findById(event.getRideRequestId())).thenReturn(Optional.of(new RideRequest()));

            // Act
            projector.handleCarpoolMatched(event);

            // Assert
            verify(carpoolRepository, never()).save(any());
            verify(rideRequestRepository, never()).save(any());
        }

        @Test
        void shouldDoNothing_whenRideRequestNotFound() {
            // Arrange
            when(carpoolRepository.findById(event.getCarpoolId())).thenReturn(Optional.of(new Carpool()));
            when(rideRequestRepository.findById(event.getRideRequestId())).thenReturn(Optional.empty());

            // Act
            projector.handleCarpoolMatched(event);

            // Assert
            verify(carpoolRepository, never()).save(any());
            verify(rideRequestRepository, never()).save(any());
        }
    }

    
    @Nested
    @DisplayName("tests for handleRequestMatched()")
    class HandleRequestMatchedTests {
        private RideRequestMatchedEvent event;

        @BeforeEach
        void setup() {
            event = RideRequestMatchedEvent.newBuilder()
                    .setMatchedCarpoolIds(List.of("carpool-1", "carpool-2", "carpool-3"))
                    .setRequestId("request-1")
                    .setRiderId("rider-1")
                    .build();
        }

        @Test
        void shouldThrowException_whenRequestNotFound() {
            // assemble
            when(rideRequestRepository.findById(event.getRequestId())).thenReturn(Optional.empty());

            // act + assert
            assertThrows(NoSuchElementException.class, () -> {
                projector.handleRequestMatched(event);
            });
        }

        @Test
        void shouldMatchPresentCarpoolsWithRequest() {
            // assemble
            RideRequest rideRequest = new RideRequest();
            Carpool carpool1 = new Carpool();
            Carpool carpool2 = new Carpool();
            when(rideRequestRepository.findById(event.getRequestId())).thenReturn(Optional.of(rideRequest));
            when(carpoolRepository.findById(event.getMatchedCarpoolIds().get(0))).thenReturn(Optional.of(carpool1));
            when(carpoolRepository.findById(event.getMatchedCarpoolIds().get(1))).thenReturn(Optional.empty());
            when(carpoolRepository.findById(event.getMatchedCarpoolIds().get(2))).thenReturn(Optional.of(carpool2));

            // act
            projector.handleRequestMatched(event);

            // assert
            assertTrue(carpool1.getPendingRideRequests().contains(rideRequest));
            assertTrue(carpool2.getPendingRideRequests().contains(rideRequest));
            assertTrue(rideRequest.getMatchedCarpools().contains(carpool1));
            assertTrue(rideRequest.getMatchedCarpools().contains(carpool2));

            verify(carpoolRepository, times(2)).save(any(Carpool.class));
            verify(carpoolRepository).save(carpool1);
            verify(carpoolRepository).save(carpool2);
            verify(rideRequestRepository).save(rideRequest);
        }
    }

    @Nested
    @DisplayName("tests for handleRequestAccepted()")
    class HandleRequestAcceptedTest {
        private RideRequestAcceptedEvent event;

        @BeforeEach
        void setup() {
            event = RideRequestAcceptedEvent.newBuilder()
                    .setCarpoolId("carpool-1")
                    .setRequestId("request-1")
                    .setRiderId("rider-1")
                    .build();
        }

        @Test
        void shouldAssignRequestAndUpdateCarpool() {
            Carpool acceptedCarpool = new Carpool();
            RideRequest request = new RideRequest();
            request.setPassengers(2);

            // Create a "matched" carpool that should be cleared
            Carpool matchedCarpool = new Carpool();
            request.getMatchedCarpools().add(matchedCarpool);
            request.getMatchedCarpools().add(acceptedCarpool);
            matchedCarpool.getPendingRideRequests().add(request);
            acceptedCarpool.getPendingRideRequests().add(request);

            when(rideRequestRepository.findById(event.getRequestId())).thenReturn(Optional.of(request));
            when(carpoolRepository.findById(event.getCarpoolId())).thenReturn(Optional.of(acceptedCarpool));

            // Act
            projector.handleRequestAccepted(event);

            // Assert
            assertEquals(RideRequestStatus.ASSIGNED, request.getStatus());
            assertEquals(acceptedCarpool, request.getAssignedCarpool());

            // All matched carpools should have been cleared
            assertTrue(request.getMatchedCarpools().isEmpty());
            assertTrue(!matchedCarpool.getPendingRideRequests().contains(request));

            // Main carpool should be updated
            assertTrue(acceptedCarpool.getConfirmedRideRequests().contains(request));
            assertTrue(!acceptedCarpool.getPendingRideRequests().contains(request));
            assertEquals(2, acceptedCarpool.getSeatsAssigned());

            // Verify repository saves
            verify(carpoolRepository).save(matchedCarpool);
            verify(carpoolRepository).save(acceptedCarpool);
            verify(rideRequestRepository).save(request);
        }
    }
    
    @Nested
    @DisplayName("tests for handleRideRequestDeclined()")
    class HandleRideRequestDeclinedTests {
        private RideRequestDeclinedEvent event;

        @BeforeEach
        void setup() {
            event = RideRequestDeclinedEvent.newBuilder()
                    .setCarpoolId("carpool-1")
                    .setRequestId("request-1")
                    .setRiderId("rider-1")
                    .build();
        }

        @Test
        void shouldRemoveRequestCarpool() {
            // assemble
            Carpool carpool = new Carpool();
            RideRequest request = new RideRequest();
            
            when(rideRequestRepository.findById(event.getRequestId())).thenReturn(Optional.of(request));
            when(carpoolRepository.findById(event.getCarpoolId())).thenReturn(Optional.of(carpool));

            // act
            projector.handleRideRequestDeclined(event);
            
            // assert
            assertTrue(!carpool.getPendingRideRequests().contains(request));
            assertTrue(!request.getMatchedCarpools().contains(carpool));

            verify(carpoolRepository).save(carpool);
            verify(rideRequestRepository).save(request);
        }

        @Test
        void shouldThrowException_whenRequestNotFound() {
            // assemble
            when(carpoolRepository.findById(event.getCarpoolId())).thenReturn(Optional.of(new Carpool()));
            when(rideRequestRepository.findById(event.getRequestId())).thenReturn(Optional.empty());

            // act + assert
            assertThrows(NoSuchElementException.class, () -> {
                projector.handleRideRequestDeclined(event);
            });
        }

        @Test
        void shouldThrowException_whenCarpoolNotFound() {
            // assemble
            when(carpoolRepository.findById(event.getCarpoolId())).thenReturn(Optional.empty());
            when(rideRequestRepository.findById(event.getRequestId())).thenReturn(Optional.of(new RideRequest()));

            // act + assert
            assertThrows(NoSuchElementException.class, () -> {
                projector.handleRideRequestDeclined(event);
            });
        }
    }

    @Nested
    @DisplayName("tests for handleCarpoolRequestInvalidated()")
    class HandleCarpoolRequestInvalidatedTests {
        private CarpoolRequestInvalidatedEvent event;

        @BeforeEach
        void setup() {
            event = CarpoolRequestInvalidatedEvent.newBuilder()
                    .setCarpoolId("carpool-1")
                    .setRideRequestId("request-1")
                    .setDriverId("rider-1")
                    .setReason("test-reason")
                    .build();
        }

        @Test
        void shouldRemoveRequest() {
            // assemble
            Carpool carpool = new Carpool();
            RideRequest request = new RideRequest();
            carpool.getPendingRideRequests().add(request);
            request.getMatchedCarpools().add(carpool);

            when(rideRequestRepository.findById(event.getRideRequestId())).thenReturn(Optional.of(request));
            when(carpoolRepository.findById(event.getCarpoolId())).thenReturn(Optional.of(carpool));
            when(carpoolRepository.findById(event.getCarpoolId())).thenReturn(Optional.of(carpool));

            // act
            projector.handleCarpoolRequestInvalidated(event);

            // assert
            assertTrue(!carpool.getPendingRideRequests().contains(request));
            assertTrue(!request.getMatchedCarpools().contains(carpool));
            verify(carpoolRepository).save(carpool);
            verify(rideRequestRepository).save(request);
        }

        @Test
        void shouldThrowBadRequestException_whenCarpoolIsAssignedToRequest() {
            // assemble
            Carpool carpool = new Carpool();
            RideRequest request = new RideRequest();
            request.setAssignedCarpool(carpool);
            when(rideRequestRepository.findById(event.getRideRequestId())).thenReturn(Optional.of(request));
            when(carpoolRepository.findById(event.getCarpoolId())).thenReturn(Optional.of(carpool));

            // act + assert
            BadRequestException ex = assertThrows(BadRequestException.class, () -> {
                projector.handleCarpoolRequestInvalidated(event);
            });
            assertEquals("Cannot invalidate carpool request. Carpool is assigned to RideRequest already.", ex.getMessage());
        }

        @Test
        void shouldThrowException_whenRequestNotFound() {
            // assemble
            when(carpoolRepository.findById(event.getCarpoolId())).thenReturn(Optional.of(new Carpool()));
            when(rideRequestRepository.findById(event.getRideRequestId())).thenReturn(Optional.empty());

            // act + assert
            assertThrows(NoSuchElementException.class, () -> {
                projector.handleCarpoolRequestInvalidated(event);
            });
        }

        @Test
        void shouldThrowException_whenCarpoolNotFound() {
            // assemble
            when(carpoolRepository.findById(event.getCarpoolId())).thenReturn(Optional.empty());
            when(rideRequestRepository.findById(event.getRideRequestId())).thenReturn(Optional.of(new RideRequest()));

            // act + assert
            assertThrows(NoSuchElementException.class, () -> {
                projector.handleCarpoolRequestInvalidated(event);
            });
        }
    }
}
