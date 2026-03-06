package com.epam.gymcrm.service;

import com.epam.gymcrm.entity.User;
import com.epam.gymcrm.exception.NotFoundException;
import com.epam.gymcrm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UserRepository userRepository;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        authService = new AuthService(userRepository);
    }

    @Test
    void authenticateShouldReturnUserWhenCredentialsAreValid() {
        User user = new User("John", "Doe", "John.Doe", "secret", true);
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(user));

        User result = authService.authenticate("John.Doe", "secret");

        assertSame(user, result);
    }

    @Test
    void authenticateShouldThrowWhenPasswordIsInvalid() {
        User user = new User("John", "Doe", "John.Doe", "secret", true);
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> authService.authenticate("John.Doe", "wrong"));
    }

    @Test
    void authenticateShouldThrowWhenUserNotFound() {
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> authService.authenticate("John.Doe", "secret"));
    }
}