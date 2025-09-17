package com.tompang.carpool.carpool_service.command.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tompang.carpool.carpool_service.command.command.carpool.AcceptCarpoolRequestCommand;
import com.tompang.carpool.carpool_service.command.command.carpool.CreateCarpoolCommand;
import com.tompang.carpool.carpool_service.command.command.carpool.DeclineCarpoolRequestCommand;
import com.tompang.carpool.carpool_service.command.command.carpool.InvalidateCarpoolRequestCommand;
import com.tompang.carpool.carpool_service.command.command.carpool.MatchCarpoolCommand;
import com.tompang.carpool.carpool_service.command.domain.carpool.CarpoolAggregate;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolDomainEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.RideRequestAggregate;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestEvent;
import com.tompang.carpool.carpool_service.command.repository.EventRepository;
import com.tompang.carpool.carpool_service.common.exceptions.BadRequestException;
import com.tompang.carpool.carpool_service.common.kurrent.StreamId;

import io.kurrent.dbclient.ReadResult;

@Service
public class CarpoolCommandHandler {

    private final Logger logger = LoggerFactory.getLogger(CarpoolCommandHandler.class);
    private final EventRepository repository;
    private final KafkaProducerService kafkaProducerService;

    public CarpoolCommandHandler(EventRepository repository, KafkaProducerService kafkaProducerService) {
        this.repository = repository;
        this.kafkaProducerService = kafkaProducerService;
    }

    public String handleCreateCarpool(CreateCarpoolCommand command) {
        logger.info("Command executed: " + command);
        CarpoolAggregate carpool = CarpoolAggregate.createCarpool(command);
        repository.appendEvents(StreamId.from(EventRepository.CarpoolConstants.STREAM_PREFIX, carpool.getId()), carpool.getUncommittedChanges());
        kafkaProducerService.publishDomainEvents(carpool.getUncommittedChanges());
        return carpool.getId();
    }

    public void handleMatchCarpool(MatchCarpoolCommand command) {
        logger.info("Command executed: " + command);
        ReadResult readResult = repository.readEvents(StreamId.from(EventRepository.CarpoolConstants.STREAM_PREFIX, command.carpoolId));
        List<CarpoolDomainEvent> events = repository.deserializeEvents(readResult.getEvents());
        CarpoolAggregate carpool = CarpoolAggregate.rehydrate(events);
        carpool.matchRequestToCarpool(command);
        repository.appendEvents(StreamId.from(EventRepository.CarpoolConstants.STREAM_PREFIX, carpool.getId()), carpool.getUncommittedChanges(), readResult.getLastStreamPosition());
        kafkaProducerService.publishDomainEvents(carpool.getUncommittedChanges());
    }

    /**
     * 1. Rehydrates relevant Carpool and RideRequest aggregates from eventstore.
     * 2. Verifies validity (RideRequest has not been assigned).
     * 3. Issues commands to aggregate and append events to store while handling concurrency(checking stream positions when appending).
     * 4. Publishes events to kafka.
     * @param command
     */
    public void handleAcceptCarpoolRequest(AcceptCarpoolRequestCommand command) {
        logger.info("Command executed: " + command);
        ReadResult carpoolReadResult = repository.readEvents(StreamId.from(EventRepository.CarpoolConstants.STREAM_PREFIX, command.carpoolId));
        ReadResult requestReadResult = repository.readEvents(StreamId.from(EventRepository.RideRequestConstants.STREAM_PREFIX, command.requestId));

        List<CarpoolDomainEvent> carpoolHistory = repository.deserializeEvents(carpoolReadResult.getEvents());
        List<RideRequestEvent> requestHistory = repository.deserializeEvents(requestReadResult.getEvents());

        CarpoolAggregate carpool = CarpoolAggregate.rehydrate(carpoolHistory);
        RideRequestAggregate request = RideRequestAggregate.rehydrate(requestHistory);

        if (!request.canAssign()) {
            throw new BadRequestException("Request " + request.getId() + " cannot be assigned a carpool, status: " + request.getStatus());
        }

        carpool.acceptRequestToCarpool(command, request.getPassengers());
        request.acceptCarpoolRequest(command);

        repository.appendEvents(new StreamId(EventRepository.CarpoolConstants.STREAM_PREFIX, carpool.getId()),
                carpool.getUncommittedChanges(), carpoolReadResult.getLastStreamPosition());
        repository.appendEvents(new StreamId(EventRepository.RideRequestConstants.STREAM_PREFIX, request.getId()),
                request.getUncommittedChanges(), requestReadResult.getLastStreamPosition());
        
        kafkaProducerService.publishDomainEvents(carpool.getUncommittedChanges());
        kafkaProducerService.publishDomainEvents(request.getUncommittedChanges());
    }

    public void handleDeclineCarpoolRequest(DeclineCarpoolRequestCommand command) {
        logger.info("Command executed: " + command);
        ReadResult carpoolReadResult = repository.readEvents(StreamId.from(EventRepository.CarpoolConstants.STREAM_PREFIX, command.carpoolId));
        ReadResult requestReadResult = repository.readEvents(StreamId.from(EventRepository.RideRequestConstants.STREAM_PREFIX, command.requestId));

        List<CarpoolDomainEvent> carpoolHistory = repository.deserializeEvents(carpoolReadResult.getEvents());
        List<RideRequestEvent> requestHistory = repository.deserializeEvents(requestReadResult.getEvents());

        CarpoolAggregate carpool = CarpoolAggregate.rehydrate(carpoolHistory);
        RideRequestAggregate request = RideRequestAggregate.rehydrate(requestHistory);

        carpool.declineRequestToCarpool(command);
        request.declineCarpoolRequest(command);

        repository.appendEvents(new StreamId(EventRepository.CarpoolConstants.STREAM_PREFIX, carpool.getId()),
                carpool.getUncommittedChanges(), carpoolReadResult.getLastStreamPosition());
        repository.appendEvents(new StreamId(EventRepository.RideRequestConstants.STREAM_PREFIX, request.getId()),
                request.getUncommittedChanges(), requestReadResult.getLastStreamPosition());
        
        kafkaProducerService.publishDomainEvents(carpool.getUncommittedChanges());
        kafkaProducerService.publishDomainEvents(request.getUncommittedChanges());
    }

    public void handleInvalidateCarpoolRequest(InvalidateCarpoolRequestCommand command) {
        logger.info("Command executed: " + command);
        ReadResult carpoolReadResult = repository.readEvents(StreamId.from(EventRepository.CarpoolConstants.STREAM_PREFIX, command.carpoolId));
        List<CarpoolDomainEvent> carpoolHistory = repository.deserializeEvents(carpoolReadResult.getEvents());
        CarpoolAggregate carpool = CarpoolAggregate.rehydrate(carpoolHistory);
        carpool.invalidateRequestToCarpool(command);
        repository.appendEvents(new StreamId(EventRepository.CarpoolConstants.STREAM_PREFIX, carpool.getId()),
                carpool.getUncommittedChanges(), carpoolReadResult.getLastStreamPosition());
        kafkaProducerService.publishDomainEvents(carpool.getUncommittedChanges());
    }

}
