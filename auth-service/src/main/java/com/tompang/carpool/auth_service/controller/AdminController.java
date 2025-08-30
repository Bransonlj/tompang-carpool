package com.tompang.carpool.auth_service.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tompang.carpool.auth_service.common.AuthHeader;
import com.tompang.carpool.auth_service.dto.RegisterRequestDto;
import com.tompang.carpool.auth_service.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth/admin")
public class AdminController {

    public final AuthService authService;

    public AdminController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/check")
    public ResponseEntity<String> testAdmin(
        @RequestHeader(name = AuthHeader.USER_ID) String userId,
        @RequestHeader(name = AuthHeader.USER_ROLES) String roles
    ) {
        return ResponseEntity.ok()
                .body(String.format("Admin check passed. X-User-Id: %s, X-User-Roles: %s", 
                        userId, 
                        roles));
    }

    @PostMapping("/register-admin")
    public ResponseEntity<Void> registerAdmin(@RequestBody @Valid RegisterRequestDto req) {
        String registeredUserId = authService.registerAdmin(req.getEmail(), req.getPassword());
        URI location = URI.create("/api/auth/admin/" + registeredUserId);
        return ResponseEntity.created(location).build();
    }
}
