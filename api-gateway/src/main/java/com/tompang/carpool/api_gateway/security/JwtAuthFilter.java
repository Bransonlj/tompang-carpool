package com.tompang.carpool.api_gateway.security;

import java.io.IOException;
import java.util.Collections;

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

    public JwtAuthFilter(JwtService jwtService) {
      this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException  {
      String authHeader = request.getHeader("Authorization");
      String token = null;
      String userId = null;
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        filterChain.doFilter(request, response);
        return;
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

          // Set the authentication in the SecurityContext
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      }

      filterChain.doFilter(request, response);
    }
}