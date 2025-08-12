package com.tompang.carpool.carpool_service.query.projector;

import java.util.Optional;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolMatchedEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestMatchedEvent;
import com.tompang.carpool.carpool_service.common.DomainTopics;
import com.tompang.carpool.carpool_service.query.entity.Carpool;
import com.tompang.carpool.carpool_service.query.entity.RideRequest;
import com.tompang.carpool.carpool_service.query.repository.CarpoolQueryRepository;
import com.tompang.carpool.carpool_service.query.repository.RideRequestQueryRepository;

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
}
