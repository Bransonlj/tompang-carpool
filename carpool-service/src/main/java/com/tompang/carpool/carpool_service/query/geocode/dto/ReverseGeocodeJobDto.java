package com.tompang.carpool.carpool_service.query.geocode.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tompang.carpool.carpool_service.command.domain.LatLong;
import com.tompang.carpool.carpool_service.query.geocode.enums.GeocodeEntity;
import com.tompang.carpool.carpool_service.query.geocode.enums.GeocodeEntityField;

public class ReverseGeocodeJobDto {
    public final LatLong location;
    public final GeocodeEntity entity;
    public final String entityId;
    public final GeocodeEntityField field;

    @JsonCreator
    public ReverseGeocodeJobDto(
        @JsonProperty("location") LatLong location,
        @JsonProperty("entity") GeocodeEntity entity,
        @JsonProperty("entityId") String entityId,
        @JsonProperty("field") GeocodeEntityField field
    ) {
        this.location = location;
        this.entity = entity;
        this.entityId = entityId;
        this.field = field;
    }

    @Override
    public String toString() {
        return "GeocodeReverseJobDto{" +
                "location=" + location +
                ", entity=" + entity +
                ", entityId='" + entityId + '\'' +
                ", field=" + field +
                '}';
    }
}
