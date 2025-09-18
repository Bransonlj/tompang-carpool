package com.tompang.carpool.user_service.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.tompang.carpool.user_service.dto.LoginResponseDto;
import com.tompang.carpool.user_service.dto.RegisterRequestDto;
import com.tompang.carpool.user_service.exception.BootstrapAdminException;
import com.tompang.carpool.user_service.exception.EmailAlreadyExistsException;
import com.tompang.carpool.user_service.exception.InvalidCredentialsException;
import com.tompang.carpool.user_service.model.User;
import com.tompang.carpool.user_service.model.UserRole;
import com.tompang.carpool.user_service.repository.UserRepository;

public class UserAuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    
    public UserAuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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
        return registeredUser.getId();
    }

    public User registerAdmin(String email, String password) {
        Optional<User> existsAdmin = userRepository.findByEmail(email);
        if (existsAdmin.isPresent()) {
            if (!existsAdmin.get().getRoles().contains(UserRole.ADMIN)) {
                throw new BootstrapAdminException("Bootstrap admin conflict: found bootstrap admin user without ADMIN role", 2);
            } else {
                throw new BootstrapAdminException("Bootstrap admin found, skipping init admin", 1);
            }
        }

        User admin = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .roles(Collections.singleton(UserRole.ADMIN))
                .build();

        return userRepository.save(admin);
    }

    /**
     * Login and returns the generated JWT token for that userId
     * @param email
     * @param password
     * @return
     */
    public LoginResponseDto login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException());
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String jwtToken = jwtService.generateToken(user.getId(), user.getRoles());
        return LoginResponseDto.builder()
                .userId(user.getId())
                .token(jwtToken)
                .roles(user.getRoles())
                .build();
    }
}
