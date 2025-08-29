package com.tompang.carpool.api_gateway.security;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    public JwtAuthFilter(JwtService jwtService) {
      this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException  {
      StringBuilder logMessage = new StringBuilder(String.format("Request: %s %s | ", 
          request.getMethod(),
          request.getRequestURI()));
      String authHeader = request.getHeader("Authorization");
      String token = null;
      String userId = null;
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        filterChain.doFilter(request, response);
        logMessage.append("Invalid authorization header");
        logger.info(logMessage.toString());
        return;
      }

      token = authHeader.substring(7); // Extract token
      userId = jwtService.extractUserId(token);
      // If the token is valid & no authentication is set in the context & token not expired 
      if (userId != null 
          && SecurityContextHolder.getContext().getAuthentication() == null 
          && !jwtService.isTokenExpired(token)
      ) {
          List<String> roles = jwtService.extractRoles(token);
          List<SimpleGrantedAuthority> authorities = roles.stream()
              .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // must prefix with ROLE_
              .toList();

          UsernamePasswordAuthenticationToken authToken =
              new UsernamePasswordAuthenticationToken(userId, null, authorities);

          // Set the authentication in the SecurityContext
          SecurityContextHolder.getContext().setAuthentication(authToken);

          // TODO add auth credentials to headers

          String rolesString = authorities.stream()
              .map(auth -> auth.getAuthority())
              .collect(Collectors.joining(","));
          logMessage.append(String.format("User: %s | Roles: %s", userId, rolesString));
      } else {
          logMessage.append("Invalid authorization token");
      }

      logger.info(logMessage.toString());
      filterChain.doFilter(request, response);
    }
}