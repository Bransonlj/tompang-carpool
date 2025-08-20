package com.tompang.carpool.carpool_service.query.projector;

import java.util.Optional;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.tompang.carpool.carpool_service.common.DomainTopics;
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

import jakarta.transaction.Transactional;

@Component
public class MatchProjector {
    private final CarpoolQueryRepository carpoolRepository;
    private final RideRequestQueryRepository rideRequestRepository;

    public MatchProjector(CarpoolQueryRepository carpoolRepository, RideRequestQueryRepository rideRequestRepository) {
        this.carpoolRepository = carpoolRepository;
        this.rideRequestRepository = rideRequestRepository;
    }

    /**
     * Adds the carpool to the ride-request matchedCarpools and 
     * adds the ride-request to the carpool pendingRideRequests.
     * @param carpool
     * @param request
     */
    private static void matchRequestAndCarpoolTogether(Carpool carpool, RideRequest request) {
        if (!carpool.getPendingRideRequests().contains(request)) {
            carpool.getPendingRideRequests().add(request);
        }

        if (!request.getMatchedCarpools().contains(carpool)) {
            request.getMatchedCarpools().add(carpool);
        }
    }

    @Transactional
    @KafkaListener(topics = DomainTopics.Carpool.CARPOOL_MATCHED, groupId = "carpool-service-query")
    public void handleCarpoolMatched(CarpoolMatchedEvent event) {
        Optional<Carpool> carpool = carpoolRepository.findById(event.getCarpoolId());
        Optional<RideRequest> request = rideRequestRepository.findById(event.getRideRequestId());

        if (carpool.isPresent() && request.isPresent()) {
            matchRequestAndCarpoolTogether(carpool.get(), request.get());
            carpoolRepository.save(carpool.get());
            rideRequestRepository.save(request.get());
        }
    }

    @Transactional
    @KafkaListener(topics = DomainTopics.RideRequest.REQUEST_MATCHED, groupId = "carpool-service-query")
    public void handleRequestMatched(RideRequestMatchedEvent event) {
        RideRequest request = rideRequestRepository.findById(event.getRequestId()).orElseThrow();
        Optional<Carpool> carpool;
        for (String carpoolId : event.getMatchedCarpoolIds()) {
            carpool = carpoolRepository.findById(carpoolId);
            if (carpool.isPresent()) {
                matchRequestAndCarpoolTogether(carpool.get(), request);
                carpoolRepository.save(carpool.get());
            }
        }

        rideRequestRepository.save(request);
    }

    /**
     * Can also subscribe to carpool-request-accepted, but we just choose this one.
     */
    @Transactional
    @KafkaListener(topics = DomainTopics.RideRequest.REQUEST_ACCEPTED, groupId = "carpool-service-query")
    public void handleRequestAccepted(RideRequestAcceptedEvent event) {
        RideRequest request = rideRequestRepository.findById(event.getRequestId()).orElseThrow();
        Carpool carpool = carpoolRepository.findById(event.getCarpoolId()).orElseThrow();

        request.setStatus(RideRequestStatus.ASSIGNED);
        request.setAssignedCarpool(carpool);

        /**
         * Clear matched carpools
         * We must clear from the owner-side(carpool) of the join relation
         */
        for (Carpool matchedCarpool : request.getMatchedCarpools()) {
            matchedCarpool.getPendingRideRequests().remove(request);
            carpoolRepository.save(matchedCarpool);
        }

        request.getMatchedCarpools().clear(); 

        carpool.getConfirmedRideRequests().add(request);
        carpool.getPendingRideRequests().remove(request);
        carpool.incrementSeatsAssigned(request.getPassengers());

        rideRequestRepository.save(request);
        carpoolRepository.save(carpool);
    }

    /**
     * Can also subscribe to carpool-request-declined, but we just choose this one.
     */
    @Transactional
    @KafkaListener(topics = DomainTopics.RideRequest.REQUEST_DECLINED, groupId = "carpool-service-query")
    public void handleRideRequestDeclined(RideRequestDeclinedEvent event) {
        RideRequest request = rideRequestRepository.findById(event.getRequestId()).orElseThrow();
        Carpool carpool = carpoolRepository.findById(event.getCarpoolId()).orElseThrow();

        request.getMatchedCarpools().remove(carpool);
        carpool.getPendingRideRequests().remove(request);

        rideRequestRepository.save(request);
        carpoolRepository.save(carpool);
    }

    @KafkaListener(topics = DomainTopics.Carpool.CARPOOL_REQUEST_INVALIDATED, groupId = "carpool-service-query")
    public void handleCarpoolInvalidated(CarpoolRequestInvalidatedEvent event) {
        RideRequest request = rideRequestRepository.findById(event.getRideRequestId()).orElseThrow();
        Carpool carpool = carpoolRepository.findById(event.getCarpoolId()).orElseThrow();
        carpool.getPendingRideRequests().remove(request);
        carpoolRepository.save(carpool);
    }

}
