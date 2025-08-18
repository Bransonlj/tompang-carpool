package com.tompang.carpool.geospatial_service.common;

public final class KafkaTopics {

    private KafkaTopics() {}

    public static final class Geocode {
        public static final String REVERSE_GEOCODE_COMPLETED = "reverse-geocode-job-completed";
    }
}
