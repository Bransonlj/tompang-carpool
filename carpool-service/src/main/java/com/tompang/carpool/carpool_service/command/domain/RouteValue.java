package com.tompang.carpool.carpool_service.command.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tompang.carpool.common.Location;
import com.tompang.carpool.common.Route;

/**
 * Value object of a route with a origin and destination
 */
public class RouteValue {
    public final LatLong origin;
    public final LatLong destination;

    @JsonCreator
    public RouteValue(
        @JsonProperty("origin") LatLong origin,
        @JsonProperty("destination") LatLong destination
    ) {
        this.origin = origin;
        this.destination = destination;
    }

    public static RouteValue from(Route route) {
        return new RouteValue(
            new LatLong(route.getOrigin().getLatitude(), route.getOrigin().getLongitude()), 
            new LatLong(route.getDestination().getLatitude(), route.getDestination().getLongitude())
        );
    }

    public Route toSchemaRoute() {
        return new Route(new Location(origin.latitude, origin.longitude), new Location(destination.latitude, destination.longitude));
    }

    @Override
    public String toString() {
        return String.format("RouteValue{origin=%s, destination=%s}", origin, destination);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Same object
        if (obj == null || getClass() != obj.getClass()) return false; // Different type

        RouteValue route = (RouteValue) obj;

        return origin != null && origin.equals(route.origin)
                && destination != null && destination.equals(route.destination);
    }
}
