package com.epam.gymcrm.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;

class SecurityAccessServiceTest {

    private final SecurityAccessService securityAccessService = new SecurityAccessService();

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAuthenticatedUsernameShouldReturnCurrentPrincipalName() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("john.doe", null)
        );

        String result = securityAccessService.getAuthenticatedUsername();

        assertEquals("john.doe", result);
    }

    @Test
    void ensureSameUserShouldPassWhenUsernamesMatch() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("john.doe", null)
        );

        assertDoesNotThrow(() -> securityAccessService.ensureSameUser("john.doe"));
    }

    @Test
    void ensureSameUserShouldThrowWhenUsernamesDoNotMatch() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("john.doe", null)
        );

        assertThrows(AccessDeniedException.class,
                () -> securityAccessService.ensureSameUser("anna.smith"));
    }
}