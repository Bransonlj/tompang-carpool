package com.tompang.carpool.profile_service.common;

public class KafkaTopics {

    private KafkaTopics() {}

    public static final class User {
        public static final String USER_REGISTERED = "user-registered";
    }

    public static final class Driver {
        public static final String DRIVER_REGISTRATION_SUCCEEDED = "driver-registration-succeeded";
        public static final String DRIVER_DEREGISTERED = "driver-deregistered";
    }

}
