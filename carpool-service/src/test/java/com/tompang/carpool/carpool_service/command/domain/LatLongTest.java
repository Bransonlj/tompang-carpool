package com.tompang.carpool.carpool_service.command.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class LatLongTest {

    @Test
    void testEquality() {
        LatLong ll1 = new LatLong(3, 4);
        LatLong ll2 = new LatLong(3, 4);
        assertEquals(ll1, ll2);
    }
}
