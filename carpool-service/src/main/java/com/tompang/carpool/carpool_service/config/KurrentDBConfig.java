package com.tompang.carpool.carpool_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.kurrent.dbclient.KurrentDBClient;
import io.kurrent.dbclient.KurrentDBClientSettings;
import io.kurrent.dbclient.KurrentDBConnectionString;

@Configuration
public class KurrentDBConfig {

    @Bean
    public KurrentDBClient kurrentDBClient() {
        KurrentDBClientSettings settings = KurrentDBConnectionString
            .parseOrThrow("kurrentdb://localhost:2113?tls=false");
        return KurrentDBClient.create(settings);
    }
}
