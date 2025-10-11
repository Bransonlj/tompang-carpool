package com.tompang.carpool.api_gateway.route;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

        @Bean
        public RouteLocator userServiceRoute(RouteLocatorBuilder builder) {
                return builder.routes()
                        .route("user-service", r -> 
                                r.path("/api/user/**")
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
        public RouteLocator driverServiceRoute(RouteLocatorBuilder builder) {
                return builder.routes()
                        .route("driver-service", r -> 
                                r.path("/api/driver/**")
                                .uri("http://localhost:4004"))
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

        @Bean
        public RouteLocator notificationServiceRoute(RouteLocatorBuilder builder) {
                return builder.routes()
                        .route("notification-service", r -> 
                                r.path("/api/notification/**")
                                .uri("http://localhost:4007"))
                        .build();
        }

        @Bean
        public RouteLocator websocketServiceRoute(RouteLocatorBuilder builder) {
                return builder.routes()
                        .route("websocket-service", r -> 
                                r.path("/socket.io/**")
                                .uri("http://localhost:4100"))
                        .build();
        }
}
