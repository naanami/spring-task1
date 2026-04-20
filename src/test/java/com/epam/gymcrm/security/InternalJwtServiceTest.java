package com.epam.gymcrm.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class InternalJwtServiceTest {

    private static final String SECRET = "thisIsInternalMicroserviceJwtSecretKey12345";
    private InternalJwtService service;

    @BeforeEach
    void setUp() {
        service = new InternalJwtService(SECRET, 3600000);
    }

    @Test
    void generateServiceTokenShouldProduceValidToken() {
        String token = service.generateServiceToken();
        assertNotNull(token);
        assertFalse(token.isBlank());
    }
}