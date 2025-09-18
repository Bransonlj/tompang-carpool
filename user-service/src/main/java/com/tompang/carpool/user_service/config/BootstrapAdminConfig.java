package com.tompang.carpool.user_service.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tompang.carpool.user_service.exception.BootstrapAdminException;
import com.tompang.carpool.user_service.model.User;
import com.tompang.carpool.user_service.service.UserAuthService;

@Configuration
public class BootstrapAdminConfig {

    private final Logger logger = LoggerFactory.getLogger(BootstrapAdminConfig.class);

    @Bean
    CommandLineRunner initAdmin(
        UserAuthService userAuthService,
        PasswordEncoder passwordEncoder
    ) {
        return args -> {
            String defaultEmail = System.getenv().getOrDefault("ADMIN_USER", "admin@ad.min");
            String defaultPassword = System.getenv().getOrDefault("ADMIN_PASS", "password");
            
            try {
                User savedAdmin = userAuthService.registerAdmin(defaultEmail, defaultPassword);
                logger.info("Default admin user created: %s %s (password from ENV)", savedAdmin.getId(), savedAdmin.getEmail());
            } catch (BootstrapAdminException exception) {
                if (exception.level <= 0) {
                    logger.debug(exception.getMessage());
                } else if (exception.level == 1) {
                    logger.info(exception.getMessage());
                } else if (exception.level == 2) {
                    logger.warn(exception.getMessage());
                } else {
                    logger.error(exception.getMessage());
                }
             }
        };
    }
}
