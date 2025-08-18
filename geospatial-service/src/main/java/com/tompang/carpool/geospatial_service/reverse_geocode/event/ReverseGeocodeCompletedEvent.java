package com.tompang.carpool.geospatial_service.reverse_geocode.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tompang.carpool.geospatial_service.onemap.dto.OnemapGeocodeResponseDto;
import com.tompang.carpool.geospatial_service.reverse_geocode.dto.ReverseGeocodeJobDto;

public class ReverseGeocodeCompletedEvent {

    public final boolean success;
    public final String address;
    public final String entity;
    public final String entityId;
    public final String field;

    @JsonCreator
    public ReverseGeocodeCompletedEvent(
        @JsonProperty("success") boolean success,
        @JsonProperty("address") String address,
        @JsonProperty("entity") String entity,
        @JsonProperty("entityId") String entityId,
        @JsonProperty("field") String field
    ) {
        this.success = success;
        this.address = address;
        this.entity = entity;
        this.entityId = entityId;
        this.field = field;
    }
    
    public static ReverseGeocodeCompletedEvent from(ReverseGeocodeJobDto dto, OnemapGeocodeResponseDto response) {
        if (response == null || response.getGeocodeInfo().isEmpty()) {
            return new ReverseGeocodeCompletedEvent(false, null, dto.entity, dto.entityId, dto.field);
        }

        // we will use the first address (closest)
        OnemapGeocodeResponseDto.GeocodeInfo firstInfo = response.getGeocodeInfo().get(0);
        StringBuilder builder = new StringBuilder();

        if (!firstInfo.getBuildingName().equals(OnemapGeocodeResponseDto.NIL)) {
            builder.append(firstInfo.getBuildingName());
            builder.append(" ");
        }

        if (!firstInfo.getBlock().equals(OnemapGeocodeResponseDto.NIL)) {
            builder.append(firstInfo.getBlock());
            builder.append(" ");
        }

        if (!firstInfo.getRoad().equals(OnemapGeocodeResponseDto.NIL)) {
            builder.append(firstInfo.getRoad());
            builder.append(" ");
        }

        if (!firstInfo.getPostalCode().equals(OnemapGeocodeResponseDto.NIL)) {
            builder.append(firstInfo.getPostalCode());
            builder.append(" ");
        }

        if (builder.isEmpty()) {
            // no result found (NIL for all fields)
            return new ReverseGeocodeCompletedEvent(false, null, dto.entity, dto.entityId, dto.field);
        }

        return new ReverseGeocodeCompletedEvent(true, builder.toString(), dto.entity, dto.entityId, dto.field);
    }

    @Override
    public String toString() {
        return "GeocodeReverseCompletedEvent{" +
            "success=" + success +
            ", address='" + address + '\'' +
            ", entity=" + entity +
            ", entityId='" + entityId + '\'' +
            ", field=" + field +
            '}';
    }
}
