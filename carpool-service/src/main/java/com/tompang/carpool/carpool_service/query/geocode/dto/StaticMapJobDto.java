package com.tompang.carpool.carpool_service.query.geocode.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tompang.carpool.geospatial.enums.GeocodeEntity;
import com.tompang.carpool.geospatial.enums.GeocodeEntityField;

import lombok.Builder;
import lombok.EqualsAndHashCode;

@Builder
@EqualsAndHashCode
public class StaticMapJobDto {
    public final double latitude;
    public final double longitude;
    public final GeocodeEntity entity;
    public final String entityId;
    public final GeocodeEntityField field;

    @JsonCreator
    public StaticMapJobDto(        
        @JsonProperty("latitude") double latitude,
        @JsonProperty("longitude") double longitude,
        @JsonProperty("entity") GeocodeEntity entity,
        @JsonProperty("entityId") String entityId,
        @JsonProperty("field") GeocodeEntityField field
    ) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.entity = entity;
        this.entityId = entityId;
        this.field = field;
    }
    @Override
    public String toString() {
        return "GeocodeReverseJobDto{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", entity=" + entity +
                ", entityId='" + entityId + '\'' +
                ", field=" + field +
                '}';
    }
}
