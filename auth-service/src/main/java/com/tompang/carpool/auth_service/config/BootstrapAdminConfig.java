package com.tompang.carpool.auth_service.config;

import java.util.Collections;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tompang.carpool.auth_service.model.User;
import com.tompang.carpool.auth_service.model.UserRole;
import com.tompang.carpool.auth_service.repository.UserRepository;

@Configuration
public class BootstrapAdminConfig {

    private final Logger logger = LoggerFactory.getLogger(BootstrapAdminConfig.class);

    @Bean
    CommandLineRunner initAdmin(UserRepository userRepository,
                                PasswordEncoder passwordEncoder) {
        return args -> {
            String defaultEmail = System.getenv().getOrDefault("ADMIN_USER", "admin@ad.min");
            String defaultPassword = System.getenv().getOrDefault("ADMIN_PASS", "password");

            // Check if an admin already exists
            Optional<User> existsAdmin = userRepository.findByEmail(defaultEmail);
            if (existsAdmin.isPresent()) {
                if (!existsAdmin.get().getRoles().contains(UserRole.ADMIN)) {
                    logger.warn("Bootstrap admin conflict: found bootstrap admin user without ADMIN role");
                } else {
                    logger.info("Bootstrap admin found, skipping init admin");
                }

                return;
            }

            User admin = User.builder()
                    .email(defaultEmail)
                    .password(passwordEncoder.encode(defaultPassword))
                    .roles(Collections.singleton(UserRole.ADMIN))
                    .build();
            User savedAdmin = userRepository.save(admin);

           logger.info("Default admin user created: %s %s (password from ENV)%n", savedAdmin.getId(), savedAdmin.getEmail());
        };
    }
}
