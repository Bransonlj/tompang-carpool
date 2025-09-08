package com.tompang.carpool.carpool_service.command.domain;

import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JSON Serializable Value Object of a location in specified by the latitude and longitude
 */
public class LatLong {
    public final double latitude;
    public final double longitude;

    @JsonCreator
    public LatLong(
        @JsonProperty("latitude") double latitude, 
        @JsonProperty("longitude") double longitude
    ) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static LatLong from(Point point) {
        return new LatLong(point.getX(), point.getY());
    }

    @Override
    public String toString() {
        return String.format("LatLong{latitude=%.6f, longitude=%.6f}", latitude, longitude);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Same object
        if (obj == null || getClass() != obj.getClass()) return false; // Different type

        LatLong latLong = (LatLong) obj;

        return latitude == latLong.latitude && longitude == latLong.longitude;
    }
}
