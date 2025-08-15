package com.tompang.carpool.carpool_service.command.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Value object of a route with a origin and destination
 */
public class Route {
    public final LatLong origin;
    public final LatLong destination;

    @JsonCreator
    public Route(
        @JsonProperty("origin") LatLong origin,
        @JsonProperty("destination") LatLong destination
    ) {
        this.origin = origin;
        this.destination = destination;
    }
}
