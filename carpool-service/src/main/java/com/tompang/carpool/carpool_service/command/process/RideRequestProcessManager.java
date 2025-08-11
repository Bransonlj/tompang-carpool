package com.tompang.carpool.carpool_service.command.process;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.tompang.carpool.carpool_service.command.command.carpool.MatchCarpoolCommand;
import com.tompang.carpool.carpool_service.command.command.ride_request.MatchRideRequestCommand;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestCreatedEvent;
import com.tompang.carpool.carpool_service.command.service.CarpoolCommandHandler;
import com.tompang.carpool.carpool_service.command.service.RideRequestCommandHandler;
import com.tompang.carpool.carpool_service.common.DomainTopics;
import com.tompang.carpool.carpool_service.query.entity.Carpool;
import com.tompang.carpool.carpool_service.query.service.CarpoolQueryService;

@Component
public class RideRequestProcessManager {


    private final CarpoolQueryService carpoolQueryService;
    private final CarpoolCommandHandler carpoolCommandHandler;
    private final RideRequestCommandHandler requestCommandHandler;

    public RideRequestProcessManager(
        CarpoolQueryService carpoolQueryService,
        CarpoolCommandHandler carpoolCommandHandler,
        RideRequestCommandHandler requestCommandHandler
    ) {
        this.carpoolQueryService = carpoolQueryService;
        this.carpoolCommandHandler = carpoolCommandHandler;
        this.requestCommandHandler = requestCommandHandler;
    }

    @KafkaListener(topics = DomainTopics.RideRequest.REQUEST_CREATED, groupId = "carpool-service-consumer")
    public void handleRideRequestCreated(RideRequestCreatedEvent event) {
        List<Carpool> matchingCarpools = carpoolQueryService.getCarpoolsByRouteInTimeRangeWithSeats(
            event.getOrigin(), event.getDestination(), event.getStartTime(), event.getEndTime(), event.getPassengers()
        );

        if (matchingCarpools.size() == 0) {
            // TODO handle no carpool match found
        }

        for (Carpool carpool : matchingCarpools) {
            carpoolCommandHandler.handleMatchCarpool(new MatchCarpoolCommand(carpool.getId(), event.getRequestId()));
        }

        requestCommandHandler.handleMatchRideRequest(
            new MatchRideRequestCommand(event.getRequestId(), matchingCarpools.stream().map(carpool -> carpool.getId()).toList())
        );
    }

}
