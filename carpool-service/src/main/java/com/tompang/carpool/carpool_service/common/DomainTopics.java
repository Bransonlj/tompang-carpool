package com.tompang.carpool.carpool_service.common;

public final class DomainTopics {

    private DomainTopics() {}

    public static final class Carpool {
        public static final String CARPOOL_CREATED = "carpool-created";
        public static final String CARPOOL_MATCHED = "carpool-matched";
    }

    public static final class RideRequest {
        public static final String REQUEST_CREATED = "ride-request-created";
        public static final String REQUEST_MATCHED = "ride-request-matched";
    }

}
