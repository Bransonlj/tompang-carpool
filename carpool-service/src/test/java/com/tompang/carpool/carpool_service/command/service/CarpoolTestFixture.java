package com.tompang.carpool.carpool_service.command.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.tompang.carpool.carpool_service.command.command.carpool.CreateCarpoolCommand;
import com.tompang.carpool.carpool_service.command.command.carpool.MatchCarpoolCommand;
import com.tompang.carpool.carpool_service.command.domain.LatLong;
import com.tompang.carpool.carpool_service.command.domain.RouteValue;

public final class CarpoolTestFixture {

    public static final String DEFAULT_DRIVER_ID = "driver-1";
    
    private final CarpoolCommandHandler commandHandler;

    public CarpoolTestFixture(CarpoolCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    public String createCarpool() {
        return createCarpool(4);
    }

    public String createCarpool(int seats) {
        CreateCarpoolCommand command = CreateCarpoolCommand.builder()
                .driverId(DEFAULT_DRIVER_ID)
                .seats(seats)
                .arrivalTime(Instant.now().truncatedTo(ChronoUnit.MILLIS))
                .route(new RouteValue(
                        new LatLong(1, 2),
                        new LatLong(3, 4)))
                .build();

        return commandHandler.handleCreateCarpool(command);
    }


    public String createCarpoolWithMatch(String requestId) {
        String carpoolId = createCarpool();

        MatchCarpoolCommand matchCommand = MatchCarpoolCommand.builder()
                .carpoolId(carpoolId)
                .requestId(requestId)
                .build();

        commandHandler.handleMatchCarpool(matchCommand);

        return carpoolId;
    }
}
