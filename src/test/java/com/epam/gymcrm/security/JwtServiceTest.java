package com.epam.gymcrm.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("thisIsATestJwtSecretKeyThatIsLongEnough123", 3600000);
    }

    @Test
    void generateTokenShouldReturnValidToken() {
        String token = jwtService.generateToken("john.doe");

        User userDetails = new User("john.doe", "pass", Collections.emptyList());

        assertEquals("john.doe", jwtService.extractUsername(token));
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValidShouldReturnFalseForDifferentUser() {
        String token = jwtService.generateToken("john.doe");

        User userDetails = new User("jane.doe", "pass", Collections.emptyList());

        assertFalse(jwtService.isTokenValid(token, userDetails));
    }
}