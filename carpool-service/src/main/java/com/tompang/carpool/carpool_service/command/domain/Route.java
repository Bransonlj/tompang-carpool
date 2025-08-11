package com.tompang.carpool.carpool_service.command.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Value object of a route with a origin and destination
 */
@AllArgsConstructor
@Getter
public class Route {
    private final String origin;
    private final String destination;
}
