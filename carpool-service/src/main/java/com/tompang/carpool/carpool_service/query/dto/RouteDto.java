package com.tompang.carpool.carpool_service.query.dto;

import org.locationtech.jts.geom.Point;

import com.tompang.carpool.carpool_service.query.entity.EventualAddress;

public class RouteDto {
    static final class Location {
        public final double latitude;
        public final double longitude;
        public final String address;

        public Location(double latitude, double longitude, String address) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.address = address;
        }
    }

    public final Location origin;
    public final Location destination;

    public RouteDto(Point origin, Point destination, EventualAddress originAddress, EventualAddress destinationAddress) {
        this.origin = new Location(origin.getX(), origin.getY(), originAddress.toString());
        this.destination = new Location(destination.getX(), destination.getY(), destinationAddress.toString());
    }
}
