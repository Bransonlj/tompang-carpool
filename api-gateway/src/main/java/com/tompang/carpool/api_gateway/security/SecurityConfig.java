package com.tompang.carpool.api_gateway.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
  private JwtAuthFilter jwtAuthFilter;

  public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
    this.jwtAuthFilter = jwtAuthFilter;
  }

  @Bean
  public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) throws Exception {
    return http
      .csrf(csrf -> csrf.disable())
      .authorizeExchange(auth -> auth
          .pathMatchers("/api/auth/public/**").permitAll()
          .pathMatchers("/api/auth/admin/**").hasRole("ADMIN")  // only admins
          .pathMatchers("/api/driver/admin/**").hasAnyRole("ADMIN")
          .pathMatchers("/socket.io/**").hasAnyRole("USER", "ADMIN")
          .anyExchange().hasRole("USER")
      )
      .cors(cors -> cors.configurationSource(request -> {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        return configuration;
      }))
      .addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
      .build();
  }

}
