package com.tompang.carpool.auth_service.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tompang.carpool.auth_service.dto.LoginRequestDto;
import com.tompang.carpool.auth_service.dto.LoginResponseDto;
import com.tompang.carpool.auth_service.dto.RegisterRequestDto;
import com.tompang.carpool.auth_service.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth/public")
public class PublicController {

    public final AuthService authService;

    public PublicController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequestDto req) {
        String registeredUserId = authService.registerUser(req);
        URI location = URI.create("/api/auth/public/" + registeredUserId);
        return ResponseEntity.created(location).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto req) {
        LoginResponseDto responseDto = authService.login(req.getEmail(), req.getPassword());
        return ResponseEntity.ok(responseDto);
    }
}
