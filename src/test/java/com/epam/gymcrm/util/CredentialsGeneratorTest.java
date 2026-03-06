package com.epam.gymcrm.util;

import com.epam.gymcrm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CredentialsGeneratorTest {

    private UserRepository userRepository;
    private CredentialsGenerator generator;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        generator = new CredentialsGenerator(userRepository, 10);
    }

    @Test
    void shouldGenerateUsernameWhenUnique() {
        when(userRepository.existsByUsername("John.Smith")).thenReturn(false);

        String username = generator.generateUniqueUsername("John", "Smith");

        assertEquals("John.Smith", username);
    }

    @Test
    void shouldAddSuffixWhenUsernameExists() {
        when(userRepository.existsByUsername("John.Smith")).thenReturn(true);
        when(userRepository.existsByUsername("John.Smith1")).thenReturn(false);

        String username = generator.generateUniqueUsername("John", "Smith");

        assertEquals("John.Smith1", username);
    }

    @Test
    void shouldGeneratePasswordWithCorrectLength() {
        String password = generator.generatePassword();
        assertEquals(10, password.length());
    }

    @Test
    void shouldThrowIfFirstNameTooShort() {
        assertThrows(IllegalArgumentException.class,
                () -> generator.generateUniqueUsername("A", "Smith"));
    }

    @Test
    void shouldThrowIfLastNameTooShort() {
        assertThrows(IllegalArgumentException.class,
                () -> generator.generateUniqueUsername("John", "S"));
    }
}