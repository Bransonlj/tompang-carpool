package com.tompang.carpool.auth_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tompang.carpool.auth_service.common.KafkaTopics;
import com.tompang.carpool.auth_service.dto.LoginRequestDto;
import com.tompang.carpool.auth_service.dto.RegisterRequestDto;
import com.tompang.carpool.auth_service.model.User;
import com.tompang.carpool.auth_service.service.AuthService;
import com.tompang.carpool.event.user.UserRegisteredEvent;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    public final AuthService authService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AuthController(AuthService authService, KafkaTemplate<String, Object> kafkaTemplate) {
        this.authService = authService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequestDto req) {
        User registeredUser = authService.register(req.getEmail(), req.getUsername(), req.getPassword());
        kafkaTemplate.send(KafkaTopics.User.USER_REGISTERED, new UserRegisteredEvent(
                registeredUser.getId(), registeredUser.getUsername(), req.getFirstName(), req.getLastName()));
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequestDto req) {
        String token = authService.login(req.getUsername(), req.getPassword());
        return ResponseEntity.ok(token);
    }
}
