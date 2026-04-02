package com.epam.gymcrm.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private final JwtService jwtService =
            new JwtService("ThisIsASecretKeyForJwtSigningThatMustBeAtLeast32BytesLong123", 3600000);

    @Test
    void generateTokenShouldCreateValidToken() {
        String token = jwtService.generateToken("john.doe");

        assertNotNull(token);
        assertEquals("john.doe", jwtService.extractUsername(token));
    }

    @Test
    void isTokenValidShouldReturnTrueForMatchingUser() {
        String token = jwtService.generateToken("john.doe");

        UserDetails userDetails = new User("john.doe", "pass", Collections.emptyList());

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValidShouldReturnFalseForDifferentUser() {
        String token = jwtService.generateToken("john.doe");

        UserDetails userDetails = new User("anna.smith", "pass", Collections.emptyList());

        assertFalse(jwtService.isTokenValid(token, userDetails));
    }
}