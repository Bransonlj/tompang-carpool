package com.tompang.carpool.api_gateway.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthFilter implements WebFilter  {

    private final JwtService jwtService;
    private final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    public JwtAuthFilter(JwtService jwtService) {
      this.jwtService = jwtService;
    }

    private Mono<Void> logAndContinue(
        WebFilterChain chain, 
        ServerWebExchange exchange, 
        StringBuilder logMessage,
        String message
    ) {
        logMessage.append(message);
        logger.info(logMessage.toString());
        return chain.filter(exchange);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        StringBuilder logMessage = new StringBuilder(
            String.format("Request: %s %s | ", 
                exchange.getRequest().getMethod(),
                exchange.getRequest().getURI().getPath()));
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null) {
            return logAndContinue(chain, exchange, logMessage, "Missing authorization header");        }

        if (!authHeader.startsWith("Bearer ")) {
            return logAndContinue(chain, exchange, logMessage, String.format("Invalid authorization header format: %s...", authHeader.substring(0, 10))); 
        }

        String token = authHeader.substring(7); // Extract token
        String userId = jwtService.extractUserId(token);
        if (userId == null) {
            return logAndContinue(chain, exchange, logMessage, "Missing userId subject from token");
        }

        if (jwtService.isTokenExpired(token)) {
            return logAndContinue(chain, exchange, logMessage, "Token is expired");
        }

        List<String> roles = jwtService.extractRoles(token);
        List<SimpleGrantedAuthority> authorities = roles.stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // must prefix with ROLE_
            .toList();

        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(userId, null, authorities);

        String rolesString = String.join(",", roles);
        logMessage.append(String.format("User: %s | Roles: %s", userId, rolesString));
        logger.info(logMessage.toString());

        ServerWebExchange mutatedExchange = exchange.mutate()
            .request(r -> r
                .header("X-User-Id", userId)
                .header("X-User-Roles", rolesString)
                .build())
            .build();

        // Set Authentication in the reactive SecurityContext
        return chain.filter(mutatedExchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken));
    }
}