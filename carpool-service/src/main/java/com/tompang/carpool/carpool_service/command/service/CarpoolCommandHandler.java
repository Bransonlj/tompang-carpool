package com.tompang.carpool.carpool_service.command.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tompang.carpool.carpool_service.command.command.carpool.CreateCarpoolCommand;
import com.tompang.carpool.carpool_service.command.command.carpool.MatchCarpoolCommand;
import com.tompang.carpool.carpool_service.command.domain.carpool.CarpoolAggregate;
import com.tompang.carpool.carpool_service.command.domain.carpool.event.CarpoolEvent;
import com.tompang.carpool.carpool_service.command.repository.CarpoolEventRepository;

@Service
public class CarpoolCommandHandler {

    private final CarpoolEventRepository repository;
    private final KafkaProducerService kafkaProducerService;

    public CarpoolCommandHandler(CarpoolEventRepository repository, KafkaProducerService kafkaProducerService) {
        this.repository = repository;
        this.kafkaProducerService = kafkaProducerService;
    }

    public String handleCreateCarpool(CreateCarpoolCommand command) {
        CarpoolAggregate carpool = CarpoolAggregate.createCarpool(command);
        repository.appendEvents(carpool.getId(), carpool.getUncommittedChanges());
        kafkaProducerService.publishDomainEvents(carpool.getUncommittedChanges());
        return carpool.getId();
    }

    public void handleMatchCarpool(MatchCarpoolCommand command) {
        List<CarpoolEvent> events = repository.readEvents(command.carpoolId);
        CarpoolAggregate carpool = CarpoolAggregate.rehydrate(events);
        carpool.matchRequestToCarpool(command);
        repository.appendEvents(carpool.getId(), carpool.getUncommittedChanges());
        kafkaProducerService.publishDomainEvents(carpool.getUncommittedChanges());
    }

}
