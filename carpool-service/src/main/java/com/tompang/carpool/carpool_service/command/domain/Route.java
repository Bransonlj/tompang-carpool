package com.tompang.carpool.carpool_service.command.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Value object of a route with a origin and destination
 */
public class Route {
    public final String origin;
    public final String destination;

    @JsonCreator
    public Route(
        @JsonProperty("origin") String origin,
        @JsonProperty("destination") String destination
    ) {
        this.origin = origin;
        this.destination = destination;
    }
}
