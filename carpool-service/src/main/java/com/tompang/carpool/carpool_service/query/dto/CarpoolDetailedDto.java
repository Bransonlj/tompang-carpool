package com.tompang.carpool.carpool_service.query.dto;

import java.util.List;

import com.tompang.carpool.carpool_service.query.entity.Carpool;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public class CarpoolDetailedDto extends CarpoolSummaryDto {
    public final List<RideRequestSummaryDto> pendingRequets;
    public final List<RideRequestSummaryDto> confirmedRequests;

    public static CarpoolDetailedDto fromEntity(Carpool carpool) {
        if (carpool == null) return null;
        return CarpoolDetailedDto.builder()
            .id(carpool.getId())
            .totalSeats(carpool.getTotalSeats())
            .seatsAssigned(carpool.getSeatsAssigned())
            .driverId(carpool.getDriverId())
            .arrivalTime(carpool.getArrivalTime())
            .origin(carpool.getOrigin())
            .destination(carpool.getDestination())
            .pendingRequets(carpool.getPendingRideRequests().stream()
                .map(request -> RideRequestSummaryDto.fromEntity(request)).toList())
            .confirmedRequests(carpool.getConfirmedRideRequests().stream()
                .map(request -> RideRequestSummaryDto.fromEntity(request)).toList())   
            .build();
    }

}
