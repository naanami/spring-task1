package com.epam.gymcrm.exception;

import com.epam.gymcrm.dto.response.ErrorResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldHandleNotFound() {
        ErrorResponse response = handler.handleNotFound(new NotFoundException("Not found"));

        assertEquals(404, response.getStatus());
        assertEquals("Not found", response.getMessage());
    }

    @Test
    void shouldHandleIllegalArgument() {
        ErrorResponse response = handler.handleIllegalArgument(new IllegalArgumentException("Bad request"));

        assertEquals(400, response.getStatus());
        assertEquals("Bad request", response.getMessage());
    }

    @Test
    void shouldHandleGenericRuntime() {
        ErrorResponse response = handler.handleGeneric(new RuntimeException("Boom"));

        assertEquals(500, response.getStatus());
        assertEquals("Something went wrong", response.getMessage());
    }

    @Test
    void shouldHandleUserBlocked() {
        ErrorResponse response = handler.handleUserBlocked(new UserBlockedException("Blocked"));

        assertEquals(423, response.getStatus());
        assertEquals("Blocked", response.getMessage());
    }
}