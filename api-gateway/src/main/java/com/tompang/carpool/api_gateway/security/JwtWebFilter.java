package com.tompang.carpool.api_gateway.security;

import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Component
public class JwtWebFilter implements WebFilter {

    private final JwtService jwtService;

    public JwtWebFilter(JwtService jwtService) {
      this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        String token = null;
        String userId = null;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        token = authHeader.substring(7); // Extract token
        userId = jwtService.extractUserId(token);
        // If the token is valid and no authentication is set in the context
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (!jwtService.isTokenExpired(token)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userId, // Principal
                                null,  // Credentials
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")) // Grant default authority (ROLE_USER)
                        );
                
                return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken));
            }
        }

        return chain.filter(exchange);
    }
}