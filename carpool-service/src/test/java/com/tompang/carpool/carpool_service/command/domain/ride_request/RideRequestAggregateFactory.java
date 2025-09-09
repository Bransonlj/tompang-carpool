package com.tompang.carpool.carpool_service.command.domain.ride_request;

import java.time.LocalDateTime;
import java.util.List;

import com.tompang.carpool.carpool_service.command.command.carpool.AcceptCarpoolRequestCommand;
import com.tompang.carpool.carpool_service.command.command.ride_request.CreateRideRequestCommand;
import com.tompang.carpool.carpool_service.command.command.ride_request.MatchRideRequestCommand;
import com.tompang.carpool.carpool_service.command.domain.LatLong;
import com.tompang.carpool.carpool_service.command.domain.RouteValue;

public class RideRequestAggregateFactory {

  /**
   * Creates a RideRequestAggregate with default values and all uncommitted events flushed.
   * @return RideRequestAggregate without any matched or assigned carpools.
   */
  public static RideRequestAggregate created() {
    // Initialize aggregate with a created ride request
    CreateRideRequestCommand command = CreateRideRequestCommand.builder()
        .riderId("rider-123")
        .passengers(2)
        .startTime(LocalDateTime.now())
        .endTime(LocalDateTime.now().plusMinutes(30))
        .route(new RouteValue(new LatLong(1, 2), new LatLong(3, 4)))
        .build();

    RideRequestAggregate aggregate = RideRequestAggregate.createRideRequest(command);
    // flush changes to reset uncommitted events
    aggregate.clearUncommittedChanges();
    return aggregate;
  }

  /**
   * Creates a RideRequestAggregate with default values and matched carpoolIds and all uncommitted events flushed.
   * @return
   */
  public static RideRequestAggregate matched() {
    RideRequestAggregate aggregate = created();
    MatchRideRequestCommand command = MatchRideRequestCommand.builder()
        .requestId(aggregate.getId())
        .matchedCarpoolIds(List.of("carpool-1", "carpool-2", "carpool-3"))
        .build();

    aggregate.matchRideRequest(command);
    aggregate.clearUncommittedChanges();
    return aggregate;
  }

  /**
   * Creates a RideRequestAggregate with default values and an assigned carpool
   * and all uncommitted events cleared.
   * @return
   */
  public static RideRequestAggregate assigned() {
    RideRequestAggregate aggregate = matched();
    AcceptCarpoolRequestCommand command = AcceptCarpoolRequestCommand.builder()
        .carpoolId(aggregate.getMatchedCarpoolsCopy().get(0))
        .requestId(aggregate.getId())
        .build();

    aggregate.acceptCarpoolRequest(command);
    aggregate.clearUncommittedChanges();
    return aggregate;
  }
}
