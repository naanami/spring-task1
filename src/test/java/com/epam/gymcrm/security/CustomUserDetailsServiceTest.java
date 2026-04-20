package com.epam.gymcrm.security;

import com.epam.gymcrm.entity.User;
import com.epam.gymcrm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    private UserRepository userRepository;
    private CustomUserDetailsService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        service = new CustomUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsernameShouldReturnUserDetails() {
        User user = new User("John", "Doe", "john.doe", "encoded", true);
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("john.doe");

        assertEquals("john.doe", details.getUsername());
        assertEquals("encoded", details.getPassword());
        assertTrue(details.isEnabled());
    }

    @Test
    void loadUserByUsernameShouldThrowWhenMissing() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("missing"));
    }
}