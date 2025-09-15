package com.tompang.carpool.carpool_service.command.domain.carpool;

import java.time.LocalDateTime;
import com.tompang.carpool.carpool_service.command.command.carpool.AcceptCarpoolRequestCommand;
import com.tompang.carpool.carpool_service.command.command.carpool.CreateCarpoolCommand;
import com.tompang.carpool.carpool_service.command.command.carpool.MatchCarpoolCommand;
import com.tompang.carpool.carpool_service.command.domain.LatLong;
import com.tompang.carpool.carpool_service.command.domain.RouteValue;

public class CarpoolAggregateFactory {
  /**
   * Creates a CarpoolAggregate with default values and all uncommitted events flushed.
   * @return CarpoolAggregate without any confirmed or pending riders.
   */
  public static CarpoolAggregate created() {
    CreateCarpoolCommand command = CreateCarpoolCommand.builder()
        .driverId("driver-1")
        .seats(4)
        .arrivalTime(LocalDateTime.now())
        .route(new RouteValue(new LatLong(1, 2), new LatLong(3, 4)))
        .build();

    CarpoolAggregate aggregate = CarpoolAggregate.createCarpool(command);
    // flush changes to reset uncommitted events
    aggregate.clearUncommittedChanges();
    return aggregate;
  }

    /**
     * Creates a CarpoolAggregate with default values and pending requestId and all uncommitted events flushed.
     * @return
     */
    public static CarpoolAggregate pending() {
        CarpoolAggregate aggregate = created();
        MatchCarpoolCommand command = MatchCarpoolCommand.builder()
            .carpoolId(aggregate.getId())
            .requestId("request-1")
            .build();

        aggregate.matchRequestToCarpool(command);
        aggregate.clearUncommittedChanges();
        return aggregate;
    }

    /**
     * Creates a CarpoolAggregate with default values and confirmed requestId
     * and all uncommitted events cleared.
     * @return
     */
    public static CarpoolAggregate confirmed() {
        CarpoolAggregate aggregate = pending();
        AcceptCarpoolRequestCommand command = AcceptCarpoolRequestCommand.builder()
            .carpoolId(aggregate.getId())
            .requestId(aggregate.getPendingRideRequestsCopy().get(0))
            .build();

        aggregate.acceptRequestToCarpool(command, 1);
        aggregate.clearUncommittedChanges();
        return aggregate;
  }
}
