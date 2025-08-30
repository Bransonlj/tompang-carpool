package com.tompang.carpool.driver_service.common;

public class KafkaTopics {

    private KafkaTopics() {}

    public static final class Driver {
        public static final String DRIVER_REGISTRATION_APPROVED = "driver-registration-approved";
        public static final String DRIVER_REGISTRATION_REJECTED = "driver-registration-rejected";
        public static final String DRIVER_DEREGISTERED = "driver-deregistered";
    }
}
