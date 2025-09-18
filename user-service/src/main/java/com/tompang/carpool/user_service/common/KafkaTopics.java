package com.tompang.carpool.user_service.common;

public class KafkaTopics {

    private KafkaTopics() {}

    public static final class Driver {
        public static final String DRIVER_REGISTRATION_APPROVED = "driver-registration-approved";
        public static final String DRIVER_DEREGISTERED = "driver-deregistered";
    }
}
