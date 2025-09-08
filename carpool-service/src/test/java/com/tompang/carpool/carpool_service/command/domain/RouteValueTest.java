package com.tompang.carpool.carpool_service.command.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class RouteValueTest {

    @Test
    void testEquality() {
        RouteValue rv1 = new RouteValue(new LatLong(1, 2), new LatLong(3, 4));
        RouteValue rv2 = new RouteValue(new LatLong(1, 2), new LatLong(3, 4));
        assertEquals(rv1, rv2);
    }
}
