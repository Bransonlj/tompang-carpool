package com.tompang.carpool.carpool_service.common;

public final class ExternalTopics {
    private ExternalTopics() {}

    public static final class Geocode {
        public static final String REVERSE_GEOCODE_COMPLETED = "reverse-geocode-job-completed";
        public static final String STATIC_MAP_COMPLETED = "static-map-job-completed";
    }
}
