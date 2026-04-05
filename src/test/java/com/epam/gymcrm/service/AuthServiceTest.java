package com.epam.gymcrm.service;

import com.epam.gymcrm.entity.User;
import com.epam.gymcrm.exception.NotFoundException;
import com.epam.gymcrm.exception.UserBlockedException;
import com.epam.gymcrm.repository.UserRepository;
import com.epam.gymcrm.security.LoginAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private LoginAttemptService loginAttemptService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        userRepository = mock(UserRepository.class);
        loginAttemptService = mock(LoginAttemptService.class);
        authService = new AuthService(authenticationManager, userRepository, loginAttemptService);
    }

    @Test
    void authenticateShouldReturnUserWhenCredentialsAreValid() {
        User user = new User("John", "Doe", "John.Doe", "$2a$hash", true);

        when(loginAttemptService.isBlocked("John.Doe")).thenReturn(false);
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        User result = authService.authenticate("John.Doe", "secret");

        assertSame(user, result);
        verify(loginAttemptService).loginSucceeded("John.Doe");
    }

    @Test
    void authenticateShouldThrowWhenPasswordIsInvalid() {
        when(loginAttemptService.isBlocked("John.Doe")).thenReturn(false, false);
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(IllegalArgumentException.class,
                () -> authService.authenticate("John.Doe", "wrong"));

        verify(loginAttemptService).loginFailed("John.Doe");
        verify(loginAttemptService, never()).loginSucceeded(anyString());
    }

    @Test
    void authenticateShouldThrowBlockedExceptionAfterThirdFailure() {
        when(loginAttemptService.isBlocked("John.Doe")).thenReturn(false, true);
        when(loginAttemptService.getRemainingBlockSeconds("John.Doe")).thenReturn(300L);

        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(UserBlockedException.class,
                () -> authService.authenticate("John.Doe", "wrong"));

        verify(loginAttemptService).loginFailed("John.Doe");
    }

    @Test
    void authenticateShouldThrowWhenUserNotFoundAfterSuccessfulAuthentication() {
        when(loginAttemptService.isBlocked("John.Doe")).thenReturn(false);
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.empty());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        assertThrows(NotFoundException.class,
                () -> authService.authenticate("John.Doe", "secret"));
    }

    @Test
    void authenticateShouldThrowWhenUserBlocked() {
        when(loginAttemptService.isBlocked("John.Doe")).thenReturn(true);
        when(loginAttemptService.getRemainingBlockSeconds("John.Doe")).thenReturn(300L);

        assertThrows(UserBlockedException.class,
                () -> authService.authenticate("John.Doe", "secret"));

        verifyNoInteractions(userRepository);
        verifyNoInteractions(authenticationManager);
    }

    @Test
    void authenticateShouldThrowWhenUserInactive() {
        when(loginAttemptService.isBlocked("John.Doe")).thenReturn(false);

        doThrow(new DisabledException("User is disabled"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(IllegalArgumentException.class,
                () -> authService.authenticate("John.Doe", "secret"));
    }
}