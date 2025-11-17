package com.tompang.carpool.api_gateway.route;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Value("${services.user}")
    private String userServiceUrl;

    @Value("${services.carpool}")
    private String carpoolServiceUrl;

    @Value("${services.driver}")
    private String driverServiceUrl;

    @Value("${services.geospatial}")
    private String geospatialServiceUrl;

    @Value("${services.notification}")
    private String notificationServiceUrl;

    @Value("${services.chat}")
    private String chatServiceUrl;

    @Value("${services.websocket}")
    private String websocketServiceUrl;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("user-service", r ->
                r.path("/api/user/**")
                 .uri(userServiceUrl))
            .route("carpool-service", r ->
                r.path("/api/carpool/**", "/api/ride-request/**")
                 .uri(carpoolServiceUrl))
            .route("driver-service", r ->
                r.path("/api/driver/**")
                 .uri(driverServiceUrl))
            .route("geospatial-service", r ->
                r.path("/api/geospatial/**")
                 .uri(geospatialServiceUrl))
            .route("notification-service", r ->
                r.path("/api/notification/**")
                 .uri(notificationServiceUrl))
            .route("chat-service", r ->
                r.path("/api/chat/**")
                 .uri(chatServiceUrl))
            .route("websocket-service", r ->
                r.path("/socket.io/**")
                 .uri(websocketServiceUrl))
            .build();
    }
}
