package com.tompang.carpool.auth_service.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tompang.carpool.auth_service.auth.JwtService;
import com.tompang.carpool.auth_service.exception.EmailAlreadyExistsException;
import com.tompang.carpool.auth_service.exception.InvalidCredentialsException;
import com.tompang.carpool.auth_service.exception.UsernameAlreadyExistsException;
import com.tompang.carpool.auth_service.model.User;
import com.tompang.carpool.auth_service.repository.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User register(String email, String username, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistsException();
        }

        if (userRepository.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyExistsException();
        }

        User user = User.builder()
                .email(email)
                .username(username)
                .password(passwordEncoder.encode(password))
                .build();
        return userRepository.save(user);
    }

    /**
     * Login and returns the generated JWT token for that userId
     * @param username
     * @param password
     * @return
     */
    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        return jwtService.generateToken(user.getId());
    }
}