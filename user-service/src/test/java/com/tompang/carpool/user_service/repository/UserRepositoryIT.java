package com.tompang.carpool.user_service.repository;

import com.tompang.carpool.user_service.common.ContainerizedIntegrationTest;
import com.tompang.carpool.user_service.model.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Transactional
public class UserRepositoryIT extends ContainerizedIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void duplicateEmail_throwsConstraintViolation() {
        User u1 = User.builder().email("test@carpool.com").build();
        User u2 = User.builder().email("test@carpool.com").build();

        userRepository.saveAndFlush(u1);

        assertThatThrownBy(() -> {
            userRepository.saveAndFlush(u2);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void findByEmail_returnsUser() {
        User createdUser = userRepository.save(
                User.builder()
                        .email("test@email.com")
                        .password("shouldhavebeenencrypted")
                        .firstName("Bob")
                        .lastName("Tan")
                        .build()
        );

        Optional<User> optionalUser = userRepository.findByEmail(createdUser.getEmail());
        assertThat(optionalUser.isPresent()).isTrue();
        assertThat(optionalUser.get().getEmail()).isEqualTo(createdUser.getEmail());
        assertThat(optionalUser.get().getId()).isEqualTo(createdUser.getId());
    }
}
