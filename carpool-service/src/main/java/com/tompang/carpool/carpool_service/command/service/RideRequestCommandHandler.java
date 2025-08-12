package com.tompang.carpool.carpool_service.command.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tompang.carpool.carpool_service.command.command.ride_request.CreateRideRequestCommand;
import com.tompang.carpool.carpool_service.command.command.ride_request.MatchRideRequestCommand;
import com.tompang.carpool.carpool_service.command.domain.ride_request.RideRequestAggregate;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestEvent;
import com.tompang.carpool.carpool_service.command.repository.RideRequestEventRepository;

@Service
public class RideRequestCommandHandler {
    private final RideRequestEventRepository repository;
    private final KafkaProducerService kafkaProducerService;

    public RideRequestCommandHandler(RideRequestEventRepository repository, KafkaProducerService kafkaProducerService) {
        this.repository = repository;
        this.kafkaProducerService = kafkaProducerService;
    }

    public String handleCreateRideRequest(CreateRideRequestCommand command) {
        RideRequestAggregate rideRequest = RideRequestAggregate.createRideRequest(command);
        repository.appendEvents(rideRequest.getId(), rideRequest.getUncommittedChanges());
        kafkaProducerService.publishDomainEvents(rideRequest.getUncommittedChanges());
        return rideRequest.getId();
    }

    public void handleMatchRideRequest(MatchRideRequestCommand command) {
        List<RideRequestEvent> events = repository.readEvents(command.requestId);
        RideRequestAggregate rideRequest = RideRequestAggregate.rehydrate(events);
        rideRequest.matchRideRequest(command);
        repository.appendEvents(rideRequest.getId(), rideRequest.getUncommittedChanges());
        kafkaProducerService.publishDomainEvents(rideRequest.getUncommittedChanges());
    }

}
