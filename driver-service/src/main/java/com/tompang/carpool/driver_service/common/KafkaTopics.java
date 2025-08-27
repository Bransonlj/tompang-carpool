package com.tompang.carpool.driver_service.common;

public class KafkaTopics {

    private KafkaTopics() {}

    public static final class Driver {
        public static final String DRIVER_REGISTRATION_SUCCEEDED = "driver-registration-succeeded";
        public static final String DRIVER_REGISTRATION_FAILED = "driver-registration-failed";
        public static final String DRIVER_DEREGISTERED = "driver-deregistered";
    }
}
