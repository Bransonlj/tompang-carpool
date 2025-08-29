package com.tompang.carpool.api_gateway.route;

import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class RouteConfig {

        @Bean
        public RouterFunction<ServerResponse> authServiceRoute() {
                return GatewayRouterFunctions.route("auth-service")
                                .route(GatewayRequestPredicates.path("/api/auth/**"), HandlerFunctions.http())
                                .before(BeforeFilterFunctions.uri("http://localhost:4002")).build();
        }

        @Bean
        public RouterFunction<ServerResponse> carpoolServiceRoute() {
                return GatewayRouterFunctions.route("carpool-service")
                                .route(GatewayRequestPredicates.path("/api/carpool/**", "/api/ride-request/**"),
                                                HandlerFunctions.http())
                                .before(BeforeFilterFunctions.uri("http://localhost:4000")).build();
        }

        @Bean
        public RouterFunction<ServerResponse> geospatialServiceRoute() {
                return GatewayRouterFunctions.route("geospatial-service")
                                .route(GatewayRequestPredicates.path("/api/geospatial/**"), HandlerFunctions.http())
                                .before(BeforeFilterFunctions.uri("http://localhost:4001")).build();
        }
}
