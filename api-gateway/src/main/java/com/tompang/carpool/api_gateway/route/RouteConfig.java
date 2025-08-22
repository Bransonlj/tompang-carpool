package com.tompang.carpool.api_gateway.route;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("carpool-service", r -> r
                        .path("/api/carpool/**")
                        .or()
                        .path("/api/ride-request/**")
                        .uri("http://localhost:4000")
                ).route("geospatial-service", r -> 
                        r.path("/api/geospatial/**")
                                .uri("http://localhost:4001")
                ).build();
    }
}
