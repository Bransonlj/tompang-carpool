package com.tompang.carpool.auth_service.service;

import java.util.Collections;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tompang.carpool.auth_service.auth.JwtService;
import com.tompang.carpool.auth_service.common.KafkaTopics;
import com.tompang.carpool.auth_service.dto.LoginResponseDto;
import com.tompang.carpool.auth_service.dto.RegisterRequestDto;
import com.tompang.carpool.auth_service.exception.EmailAlreadyExistsException;
import com.tompang.carpool.auth_service.exception.InvalidCredentialsException;
import com.tompang.carpool.auth_service.model.User;
import com.tompang.carpool.auth_service.model.UserRole;
import com.tompang.carpool.auth_service.repository.UserRepository;
import com.tompang.carpool.event.user.UserRegisteredEvent;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       KafkaTemplate<String, Object> kafkaTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.kafkaTemplate = kafkaTemplate;
    }

    public String registerUser(RegisterRequestDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException();
        }

        User user = User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build(); // default role is user
        User registeredUser = userRepository.save(user);
        kafkaTemplate.send(KafkaTopics.User.USER_REGISTERED, new UserRegisteredEvent(
                registeredUser.getId(), dto.getFirstName(), dto.getLastName()));
        return registeredUser.getId();
    }

    public String registerAdmin(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistsException();
        }

        User user = User.builder()
                .email(email)
                .roles(Collections.singleton(UserRole.ADMIN))
                .password(passwordEncoder.encode(password))
                .build();
        return userRepository.save(user).getId();
    }

    /**
     * Login and returns the generated JWT token for that userId
     * @param email
     * @param password
     * @return
     */
    public LoginResponseDto login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        String jwtToken = jwtService.generateToken(user.getId(), user.getRoles());
        return LoginResponseDto.builder()
                .userId(user.getId())
                .token(jwtToken)
                .roles(user.getRoles())
                .build();
    }
}