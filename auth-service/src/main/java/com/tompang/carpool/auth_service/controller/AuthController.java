package com.tompang.carpool.auth_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tompang.carpool.auth_service.dto.LoginRequestDto;
import com.tompang.carpool.auth_service.dto.RegisterRequestDto;
import com.tompang.carpool.auth_service.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    public final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDto req) {
        authService.register(req.getEmail(), req.getUsername(), req.getPassword());
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto req) {
        String token = authService.login(req.getUsername(), req.getPassword());
        return ResponseEntity.ok(token);
    }
}
