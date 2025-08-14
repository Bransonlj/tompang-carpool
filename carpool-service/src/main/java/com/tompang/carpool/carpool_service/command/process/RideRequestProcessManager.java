package com.tompang.carpool.carpool_service.command.process;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.tompang.carpool.carpool_service.command.command.carpool.InvalidateCarpoolRequestCommand;
import com.tompang.carpool.carpool_service.command.command.carpool.MatchCarpoolCommand;
import com.tompang.carpool.carpool_service.command.command.ride_request.FailRideRequestCommand;
import com.tompang.carpool.carpool_service.command.command.ride_request.MatchRideRequestCommand;
import com.tompang.carpool.carpool_service.command.domain.ride_request.RideRequestAggregate;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestAcceptedEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestCreatedEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestDeclinedEvent;
import com.tompang.carpool.carpool_service.command.domain.ride_request.event.RideRequestEvent;
import com.tompang.carpool.carpool_service.command.repository.EventRepository;
import com.tompang.carpool.carpool_service.command.service.CarpoolCommandHandler;
import com.tompang.carpool.carpool_service.command.service.RideRequestCommandHandler;
import com.tompang.carpool.carpool_service.common.DomainTopics;
import com.tompang.carpool.carpool_service.common.kurrent.StreamId;
import com.tompang.carpool.carpool_service.query.entity.Carpool;
import com.tompang.carpool.carpool_service.query.service.CarpoolQueryService;

import io.kurrent.dbclient.ReadResult;

@Component
public class RideRequestProcessManager {

    private final CarpoolQueryService carpoolQueryService;
    private final CarpoolCommandHandler carpoolCommandHandler;
    private final RideRequestCommandHandler requestCommandHandler;
    private final EventRepository eventRepository;

    public RideRequestProcessManager(
        CarpoolQueryService carpoolQueryService,
        CarpoolCommandHandler carpoolCommandHandler,
        RideRequestCommandHandler requestCommandHandler,
        EventRepository eventRepository
    ) {
        this.carpoolQueryService = carpoolQueryService;
        this.carpoolCommandHandler = carpoolCommandHandler;
        this.requestCommandHandler = requestCommandHandler;
        this.eventRepository = eventRepository;
    }

    @KafkaListener(topics = DomainTopics.RideRequest.REQUEST_CREATED, groupId = "carpool-service-consumer")
    public void handleRideRequestCreated(RideRequestCreatedEvent event) {
        List<Carpool> matchingCarpools = carpoolQueryService.getCarpoolsByRouteInTimeRangeWithSeats(
            event.route.origin, event.route.destination, event.startTime, event.endTime, event.passengers
        );

        if (matchingCarpools.size() == 0) {
            // fail request on no match found
            requestCommandHandler.handleFailRideRequest(new FailRideRequestCommand(event.requestId, "No carpool matches found"));
        }

        for (Carpool carpool : matchingCarpools) {
            carpoolCommandHandler.handleMatchCarpool(new MatchCarpoolCommand(carpool.getId(), event.requestId));
        }

        requestCommandHandler.handleMatchRideRequest(
            new MatchRideRequestCommand(event.requestId, matchingCarpools.stream().map(carpool -> carpool.getId()).toList())
        );
    }

    /**
     * When a request is accepted by a carpool, the commands invoked by the aggregates produce a REQUEST_ACCEPTED and CARPOOL_REQUEST_ACCEPTED event.
     * Both of these event can be used to trigger this process, but we shall just use REQUEST_ACCEPTED.
     * The process will then call an InvalidateCarpoolRequestCommand to all other carpools matched with that request.
     * @param event
     */
    @KafkaListener(topics = DomainTopics.RideRequest.REQUEST_ACCEPTED, groupId = "carpool-service-consumer")
    public void handleRideRequestAccepted(RideRequestAcceptedEvent event) {
        ReadResult readResult = eventRepository.readEvents(StreamId.from(EventRepository.RideRequestConstants.STREAM_PREFIX, event.requestId));
        List<RideRequestEvent> history = eventRepository.deserializeEvents(readResult.getEvents(), EventRepository.RideRequestConstants.EVENT_TYPE_MAP);
        RideRequestAggregate request = RideRequestAggregate.rehydrate(history);
        for (String carpoolId : request.getMatchedCarpoolsCopy()) {
            // invoke command for each carpool that is not the accepted carpool
            if (!carpoolId.equals(event.carpoolId)) {
                carpoolCommandHandler.handleInvalidateCarpoolRequest(new InvalidateCarpoolRequestCommand(carpoolId, event.requestId, "RideRequest has been accepted by another Carpool"));
            }
        }

    }

    /**
     * When a request is declined, we need to check if the request still has any pending carpool matches.
     * If it does not, then we need to invoke the FailRideRequestCommand.
     * @param event
     */
    @KafkaListener(topics = DomainTopics.RideRequest.REQUEST_DECLINED, groupId = "carpool-service-consumer")
    public void handleRideRequestDeclined(RideRequestDeclinedEvent event) {
        ReadResult readResult = eventRepository.readEvents(StreamId.from(EventRepository.RideRequestConstants.STREAM_PREFIX, event.requestId));
        List<RideRequestEvent> history = eventRepository.deserializeEvents(readResult.getEvents(), EventRepository.RideRequestConstants.EVENT_TYPE_MAP);
        RideRequestAggregate request = RideRequestAggregate.rehydrate(history);
        if (request.getMatchedCarpoolsCopy().isEmpty()) {
            // no more matched carpools, invoke fail
            requestCommandHandler.handleFailRideRequest(new FailRideRequestCommand(request.getId(), "All matched carpools declined the request"));
        }

    }

}
