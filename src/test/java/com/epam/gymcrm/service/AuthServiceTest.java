package com.epam.gymcrm.service;

import com.epam.gymcrm.entity.User;
import com.epam.gymcrm.exception.NotFoundException;
import com.epam.gymcrm.exception.UserBlockedException;
import com.epam.gymcrm.repository.UserRepository;
import com.epam.gymcrm.security.LoginAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private LoginAttemptService loginAttemptService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        loginAttemptService = mock(LoginAttemptService.class);
        authService = new AuthService(userRepository, passwordEncoder, loginAttemptService);
    }

    @Test
    void authenticateShouldReturnUserWhenCredentialsAreValid() {
        User user = new User("John", "Doe", "John.Doe", "$2a$hash", true);

        when(loginAttemptService.isBlocked("John.Doe")).thenReturn(false);
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "$2a$hash")).thenReturn(true);

        User result = authService.authenticate("John.Doe", "secret");

        assertSame(user, result);
        verify(loginAttemptService).loginSucceeded("John.Doe");
    }

    @Test
    void authenticateShouldThrowWhenPasswordIsInvalid() {
        User user = new User("John", "Doe", "John.Doe", "$2a$hash", true);

        when(loginAttemptService.isBlocked("John.Doe")).thenReturn(false);
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "$2a$hash")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> authService.authenticate("John.Doe", "wrong"));

        verify(loginAttemptService).loginFailed("John.Doe");
        verify(loginAttemptService, never()).loginSucceeded(anyString());
    }

    @Test
    void authenticateShouldThrowWhenUserNotFound() {
        when(loginAttemptService.isBlocked("John.Doe")).thenReturn(false);
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> authService.authenticate("John.Doe", "secret"));
    }

    @Test
    void authenticateShouldThrowWhenUserBlocked() {
        when(loginAttemptService.isBlocked("John.Doe")).thenReturn(true);

        assertThrows(UserBlockedException.class,
                () -> authService.authenticate("John.Doe", "secret"));

        verifyNoInteractions(userRepository);
    }
}