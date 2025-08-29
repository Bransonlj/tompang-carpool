package com.tompang.carpool.api_gateway.route;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

        @Bean
        public RouteLocator authServiceRoute(RouteLocatorBuilder builder) {
                return builder.routes()
                        .route("auth-service", r -> 
                                r.path("/api/auth/**")
                                .uri("http://localhost:4002"))
                        .build();
        }

        @Bean
        public RouteLocator carpoolServiceRoute(RouteLocatorBuilder builder) {
                return builder.routes()
                        .route("carpool-service", r ->
                                r.path("/api/carpool/**", "/api/ride-request/**") // TODO fix carpool-service route names
                                .uri("http://localhost:4000"))
                        .build();
        }

        @Bean
        public RouteLocator geospatialServiceRoute(RouteLocatorBuilder builder) {
                return builder.routes()
                        .route("geospatial-service", r -> 
                                r.path("/api/geospatial/**")
                                .uri("http://localhost:4001"))
                        .build();
        }
}
