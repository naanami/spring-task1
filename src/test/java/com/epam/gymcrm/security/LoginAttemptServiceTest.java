package com.epam.gymcrm.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginAttemptServiceTest {

    private final LoginAttemptService service = new LoginAttemptService();

    @Test
    void userShouldNotBeBlockedInitially() {
        assertFalse(service.isBlocked("john.doe"));
    }

    @Test
    void userShouldBeBlockedAfterThreeFailures() {
        service.loginFailed("john.doe");
        service.loginFailed("john.doe");
        service.loginFailed("john.doe");

        assertTrue(service.isBlocked("john.doe"));
    }

    @Test
    void successfulLoginShouldResetAttempts() {
        service.loginFailed("john.doe");
        service.loginFailed("john.doe");

        service.loginSucceeded("john.doe");

        assertFalse(service.isBlocked("john.doe"));
    }
}