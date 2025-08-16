package com.tompang.carpool.carpool_service.common;

public final class DomainTopics {

    private DomainTopics() {}

    public static final class Carpool {
        public static final String CARPOOL_CREATED = "carpool-created";
        public static final String CARPOOL_MATCHED = "carpool-matched";
        public static final String CARPOOL_REQUEST_ACCEPTED = "carpool-request-accepted";
        public static final String CARPOOL_REQUEST_DECLINED = "carpool-request-declined";
        public static final String CARPOOL_REQUEST_INVALIDATED = "carpool-request-invalidated";
    }

    public static final class RideRequest {
        public static final String REQUEST_CREATED = "ride-request-created";
        public static final String REQUEST_MATCHED = "ride-request-matched";
        public static final String REQUEST_FAILED = "ride-request-failed";
        public static final String REQUEST_ACCEPTED = "ride-request-accepted";
        public static final String REQUEST_DECLINED = "ride-request-declined";
    }

    /*
     * This really isnt considered a domain topic...
     */
    public static final class Geocode {
        public static final String GEOCODE_REVERSE_COMPLETED = "geocode-reverse-completed";
    }

}
