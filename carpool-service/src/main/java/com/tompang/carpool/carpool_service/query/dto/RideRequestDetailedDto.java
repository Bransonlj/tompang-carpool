package com.tompang.carpool.carpool_service.query.dto;

import java.util.List;

import com.tompang.carpool.carpool_service.command.domain.LatLong;
import com.tompang.carpool.carpool_service.command.domain.Route;
import com.tompang.carpool.carpool_service.query.entity.RideRequest;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public class RideRequestDetailedDto extends RideRequestSummaryDto {
    public final List<CarpoolSummaryDto> matchedCarpools;
    public final CarpoolSummaryDto assignedCarpool;

    public static RideRequestDetailedDto fromEntity(RideRequest request) {
        if (request == null) return null;
        return RideRequestDetailedDto.builder()
                .id(request.getId())
                .passengers(request.getPassengers())
                .riderId(request.getRiderId())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .route(new Route(LatLong.from(request.getOrigin()), LatLong.from(request.getDestination())))
                .status(request.getStatus())
                .matchedCarpools(request.getMatchedCarpools().stream()
                    .map(carpool -> CarpoolSummaryDto.fromEntity(carpool)).toList())
                .assignedCarpool(CarpoolSummaryDto.fromEntity(request.getAssignedCarpool()))
                .build();
    }
}
