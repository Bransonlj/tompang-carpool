package com.tompang.carpool.carpool_service.query.dto;

import java.time.LocalDateTime;

import com.tompang.carpool.carpool_service.query.entity.Carpool;

import lombok.experimental.SuperBuilder;

/**
 * Summarized DTO for Carpool entity without RideRequest data.
 */
@SuperBuilder
public class CarpoolSummaryDto {
    public final String id;
    public final int totalSeats;
    public final int seatsAssigned;
    public final String driverId;
    public final LocalDateTime arrivalTime;
    public final RouteDto route;
    public String originImageUrl;
    public String destinationImageUrl;

    public static CarpoolSummaryDto fromEntity(Carpool carpool) {
        if (carpool == null) return null;
        return CarpoolSummaryDto.builder()
                .id(carpool.getId())
                .totalSeats(carpool.getTotalSeats())
                .seatsAssigned(carpool.getSeatsAssigned())
                .driverId(carpool.getDriverId())
                .arrivalTime(carpool.getArrivalTime())
                .route(new RouteDto(carpool.getOrigin(), carpool.getDestination(), carpool.getOriginEventualAddress(), carpool.getDestinationEventualAddress()))
                .build();
    }

    public void setOriginImageUrl(String originImageUrl) {
        this.originImageUrl = originImageUrl;
    }

    public void setDestinationImageUrl(String destinationImageUrl) {
        this.destinationImageUrl = destinationImageUrl;
    }

}
