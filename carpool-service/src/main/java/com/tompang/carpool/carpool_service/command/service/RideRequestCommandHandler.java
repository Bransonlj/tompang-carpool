package com.tompang.carpool.carpool_service.command.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tompang.carpool.carpool_service.command.command.ride_request.CreateRideRequestCommand;
import com.tompang.carpool.carpool_service.command.command.ride_request.FailRideRequestCommand;
import com.tompang.carpool.carpool_service.command.command.ride_request.MatchRideRequestCommand;
import com.tompang.carpool.carpool_service.command.domain.ride_request.RideRequestAggregate;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestEvent;
import com.tompang.carpool.carpool_service.command.repository.EventRepository;
import com.tompang.carpool.carpool_service.common.kurrent.StreamId;

import io.kurrent.dbclient.ReadResult;

@Service
public class RideRequestCommandHandler {
    private final EventRepository repository;
    private final KafkaProducerService kafkaProducerService;
    private final Logger logger = LoggerFactory.getLogger(RideRequestCommandHandler.class);

    public RideRequestCommandHandler(EventRepository repository, KafkaProducerService kafkaProducerService) {
        this.repository = repository;
        this.kafkaProducerService = kafkaProducerService;
    }

    public String handleCreateRideRequest(CreateRideRequestCommand command) {
        RideRequestAggregate rideRequest = RideRequestAggregate.createRideRequest(command);
        repository.appendEvents(StreamId.from(EventRepository.RideRequestConstants.STREAM_PREFIX, rideRequest.getId()), rideRequest.getUncommittedChanges());
        kafkaProducerService.publishDomainEvents(rideRequest.getUncommittedChanges());
        return rideRequest.getId();
    }

    public void handleMatchRideRequest(MatchRideRequestCommand command) {        
        ReadResult readResult = repository.readEvents(StreamId.from(EventRepository.RideRequestConstants.STREAM_PREFIX, command.requestId));
        List<RideRequestEvent> history = repository.deserializeEvents(readResult.getEvents());
        RideRequestAggregate request = RideRequestAggregate.rehydrate(history);
        request.matchRideRequest(command);
        repository.appendEvents(StreamId.from(EventRepository.RideRequestConstants.STREAM_PREFIX, request.getId()), request.getUncommittedChanges(), readResult.getLastStreamPosition());
        kafkaProducerService.publishDomainEvents(request.getUncommittedChanges());
    }

    public void handleFailRideRequest(FailRideRequestCommand command) {
        this.logger.info(command.toString());
        ReadResult readResult = repository.readEvents(StreamId.from(EventRepository.RideRequestConstants.STREAM_PREFIX, command.requestId));
        List<RideRequestEvent> history = repository.deserializeEvents(readResult.getEvents());
        RideRequestAggregate request = RideRequestAggregate.rehydrate(history);
        request.failRideRequest(command);
        repository.appendEvents(StreamId.from(EventRepository.RideRequestConstants.STREAM_PREFIX, request.getId()), request.getUncommittedChanges(), readResult.getLastStreamPosition());
        kafkaProducerService.publishDomainEvents(request.getUncommittedChanges());
    }

}
