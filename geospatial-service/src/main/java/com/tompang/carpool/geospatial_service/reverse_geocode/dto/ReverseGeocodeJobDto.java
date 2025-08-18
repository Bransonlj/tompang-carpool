package com.tompang.carpool.geospatial_service.reverse_geocode.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReverseGeocodeJobDto {
    public final Location location;
    public final String entity;
    public final String entityId;
    public final String field;

    public static class Location {
        public final double latitude;
        public final double longitude;

        @JsonCreator
        public Location(
            @JsonProperty("latitude") double latitude, 
            @JsonProperty("longitude") double longitude
        ) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    @JsonCreator
    public ReverseGeocodeJobDto(
        @JsonProperty("location") Location location,
        @JsonProperty("entity") String entity,
        @JsonProperty("entityId") String entityId,
        @JsonProperty("field") String field
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

