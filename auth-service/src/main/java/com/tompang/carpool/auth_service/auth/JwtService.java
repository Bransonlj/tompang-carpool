package com.tompang.carpool.auth_service.auth;

import java.util.Date;
import java.util.Set;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tompang.carpool.auth_service.model.UserRole;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${jwt.secret-key}")
    private String secretKey;
    private final long EXPIRATION_MS = 86400000; // 24h

    private SecretKey getSigningKey() {
        // Converts secret string into a proper HMAC SHA key
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(String userId, Set<UserRole> roles) {
        return Jwts.builder()
                .subject(userId)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(getSigningKey())
                .compact();
    }
}
