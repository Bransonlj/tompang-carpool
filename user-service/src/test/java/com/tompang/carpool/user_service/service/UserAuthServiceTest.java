package com.tompang.carpool.user_service.service;

import com.tompang.carpool.user_service.dto.LoginResponseDto;
import com.tompang.carpool.user_service.dto.RegisterRequestDto;
import com.tompang.carpool.user_service.exception.BootstrapAdminException;
import com.tompang.carpool.user_service.exception.EmailAlreadyExistsException;
import com.tompang.carpool.user_service.exception.InvalidCredentialsException;
import com.tompang.carpool.user_service.model.User;
import com.tompang.carpool.user_service.model.UserRole;
import com.tompang.carpool.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserAuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserAuthService userAuthService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class RegisterUserTests {
        @Test
        void registersUserSuccessfully() {
            String userId = "u123";
            RegisterRequestDto dto = RegisterRequestDto.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email("john@test.com")
                    .password("password")
                    .build();

            when(userRepository.findByEmail(dto.email()))
                    .thenReturn(Optional.empty());

            when(passwordEncoder.encode("password"))
                    .thenReturn("encoded-password");

            User savedUser = User.builder()
                    .id(userId)
                    .email(dto.email())
                    .password("encoded-password")
                    .build();

            when(userRepository.save(any(User.class)))
                    .thenReturn(savedUser);

            String result = userAuthService.registerUser(dto);

            assertThat(result).isEqualTo(userId);
            verify(userRepository).save(any(User.class));
        }

        @Test
        void throwsEmailAlreadyExistsException() {
            RegisterRequestDto dto = RegisterRequestDto.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email("john@test.com")
                    .password("password")
                    .build();
            User user = User.builder()
                    .email(dto.email())
                    .build();
            when(userRepository.findByEmail(dto.email()))
                    .thenReturn(Optional.of(user));

            assertThatThrownBy(() -> userAuthService.registerUser(dto))
                    .isInstanceOf(EmailAlreadyExistsException.class);
        }
    }

    @Nested
    class RegisterAdminTest {
        @Test
        void registerAdminSuccessfully() {
            String email = "admin@test.com";
            String password = "password";
            when(userRepository.findByEmail(email))
                    .thenReturn(Optional.empty());

            when(passwordEncoder.encode(password))
                    .thenReturn("encoded-password");

            User admin = User.builder()
                    .email(email)
                    .password("encoded-password")
                    .roles(Set.of(UserRole.ADMIN))
                    .build();

            when(userRepository.save(any(User.class)))
                    .thenReturn(admin);

            User savedAdmin = userAuthService.registerAdmin(email, password);

            verify(userRepository).save(eq(admin));
            assertThat(savedAdmin.getRoles()).contains(UserRole.ADMIN);
        }

        @Test
        void throwsExceptionIfEmailInUseMyNonAdmin() {
            String email = "admin@test.com";
            User existingUser = User.builder()
                    .roles(Set.of(UserRole.USER))
                    .build();

            when(userRepository.findByEmail(email))
                    .thenReturn(Optional.of(existingUser));

            assertThatThrownBy(() ->
                    userAuthService.registerAdmin(email, "password"))
                    .isInstanceOf(BootstrapAdminException.class)
                    .hasMessageContaining("conflict");
        }

        @Test
        void throwsExceptionIfAdminAlreadyExists() {
            String email = "admin@test.com";
            User existingAdmin = User.builder()
                    .roles(Set.of(UserRole.ADMIN))
                    .build();

            when(userRepository.findByEmail(email))
                    .thenReturn(Optional.of(existingAdmin));

            assertThatThrownBy(() ->
                    userAuthService.registerAdmin(email, "password"))
                    .isInstanceOf(BootstrapAdminException.class)
                    .hasMessageContaining("skipping");
        }
    }

    @Nested
    class LoginTests {
        @Test
        void returnsLoginResponseSuccessfully() {
            String email = "user@test.com";
            String password = "password";
            String encodedPassword = "encoded-password";
            User user = User.builder()
                    .id("u123")
                    .email(email)
                    .password(encodedPassword)
                    .roles(Set.of(UserRole.USER))
                    .build();

            when(userRepository.findByEmail(user.getEmail()))
                    .thenReturn(Optional.of(user));

            when(passwordEncoder.matches(password, encodedPassword))
                    .thenReturn(true);

            String jwtToken = "jwt-token";
            when(jwtService.generateToken(user.getId(), user.getRoles()))
                    .thenReturn(jwtToken);

            LoginResponseDto response =
                    userAuthService.login(email, password);

            assertThat(response.getUserId()).isEqualTo(user.getId());
            assertThat(response.getToken()).isEqualTo(jwtToken);
            assertThat(response.getRoles()).contains(UserRole.USER);
        }

        @Test
        void throwsInvalidCredentialsExceptionIfUserNotFound() {
            String email = "user@test.com";
            when(userRepository.findByEmail(email))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    userAuthService.login(email, "password"))
                    .isInstanceOf(InvalidCredentialsException.class);
        }

        @Test
        void throwsInvalidCredentialsExceptionIfWrongPassword() {
            String email = "user@test.com";
            String password = "password";
            String encodedPassword = "encoded-password";
            User user = User.builder()
                    .email(email)
                    .password(encodedPassword)
                    .build();

            when(userRepository.findByEmail(email))
                    .thenReturn(Optional.of(user));

            when(passwordEncoder.matches(password, encodedPassword))
                    .thenReturn(false);

            assertThatThrownBy(() ->
                    userAuthService.login(email, password))
                    .isInstanceOf(InvalidCredentialsException.class);
        }
    }
}