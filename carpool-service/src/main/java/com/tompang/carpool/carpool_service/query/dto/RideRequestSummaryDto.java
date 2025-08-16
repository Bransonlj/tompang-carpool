package com.tompang.carpool.carpool_service.query.dto;

import java.time.LocalDateTime;

import com.tompang.carpool.carpool_service.query.entity.RideRequest;
import com.tompang.carpool.carpool_service.query.entity.RideRequestStatus;

import lombok.experimental.SuperBuilder;

/**
 * Summarized DTO for RideRequest entity without Carpool data.
 */
@SuperBuilder
public class RideRequestSummaryDto {
    public final String id;
    public final int passengers;
    public final String riderId;
    public final LocalDateTime startTime;
    public final LocalDateTime endTime;
    public final RouteDto route;
    public final RideRequestStatus status;

    public static RideRequestSummaryDto fromEntity(RideRequest request) {
        if (request == null) return null;
        return RideRequestSummaryDto.builder()
                .id(request.getId())
                .passengers(request.getPassengers())
                .riderId(request.getRiderId())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .route(new RouteDto(request.getOrigin(), request.getDestination(), request.getOriginEventualAddress(), request.getDestinationEventualAddress()))
                .status(request.getStatus())
                .build();
    }
}
