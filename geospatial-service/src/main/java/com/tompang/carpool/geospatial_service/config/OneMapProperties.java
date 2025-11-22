package com.tompang.carpool.geospatial_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "onemap")
@Getter
@Setter
public class OneMapProperties {
    private String email;
    private String password;
}
