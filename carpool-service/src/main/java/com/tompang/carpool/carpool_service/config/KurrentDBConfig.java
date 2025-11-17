package com.tompang.carpool.carpool_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.kurrent.dbclient.KurrentDBClient;
import io.kurrent.dbclient.KurrentDBClientSettings;
import io.kurrent.dbclient.KurrentDBConnectionString;
import io.kurrent.dbclient.KurrentDBPersistentSubscriptionsClient;

@Configuration
public class KurrentDBConfig {

    @Value("${kurrentdb.host}")
    private String host;

    @Value("${kurrentdb.port}")
    private int port;

    @Value("${kurrentdb.tls:false}")
    private boolean tls;

    private String buildConnectionString() {
        return String.format("kurrentdb://%s:%d?tls=%s", host, port, tls);
    }

    @Bean
    public KurrentDBClient kurrentDBClient() {
        KurrentDBClientSettings settings = KurrentDBConnectionString
            .parseOrThrow(buildConnectionString());
        return KurrentDBClient.create(settings);
    }

    @Bean
    public KurrentDBPersistentSubscriptionsClient kurrentDBPersistentSubscriptionsClient() {
        KurrentDBClientSettings settings = KurrentDBConnectionString
            .parseOrThrow(buildConnectionString());
        return KurrentDBPersistentSubscriptionsClient.create(settings);
    }
}
