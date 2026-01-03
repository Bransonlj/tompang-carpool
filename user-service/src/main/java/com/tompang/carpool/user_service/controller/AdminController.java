package com.tompang.carpool.user_service.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tompang.carpool.user_service.common.AuthHeader;
import com.tompang.carpool.user_service.dto.RegisterRequestDto;
import com.tompang.carpool.user_service.model.User;
import com.tompang.carpool.user_service.service.UserAuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user/auth/admin")
public class AdminController {

    public final UserAuthService authService;

    public AdminController(UserAuthService authService) {
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
    public ResponseEntity<Void> registerAdmin(
        @RequestHeader(name = AuthHeader.USER_ID) String userId,
        @RequestHeader(name = AuthHeader.USER_ROLES) String roles,
        @RequestBody @Valid RegisterRequestDto req
    ) {
        // TODO verify admin role
        User registeredAdmin = authService.registerAdmin(req);
        URI location = URI.create("/api/auth/admin/" + registeredAdmin.getId());
        return ResponseEntity.created(location).build();
    }
}
