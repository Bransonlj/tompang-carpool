package com.tompang.carpool.auth_service.common;

public class KafkaTopics {

    private KafkaTopics() {}

    public static final class User {
        public static final String USER_REGISTERED = "user-registered";
    }
}
