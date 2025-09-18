package com.tompang.carpool.user_service.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tompang.carpool.user_service.dto.LoginRequestDto;
import com.tompang.carpool.user_service.dto.LoginResponseDto;
import com.tompang.carpool.user_service.dto.RegisterRequestDto;
import com.tompang.carpool.user_service.service.UserAuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user/auth/public")
public class PublicController {

    public final UserAuthService authService;

    public PublicController(UserAuthService authService) {
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
