package com.epam.gymcrm.service;

import com.epam.gymcrm.dto.response.GeneratedCredentials;
import com.epam.gymcrm.entity.User;
import com.epam.gymcrm.exception.NotFoundException;
import com.epam.gymcrm.repository.UserRepository;
import com.epam.gymcrm.util.CredentialsGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private CredentialsGenerator credentialsGenerator;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        credentialsGenerator = mock(CredentialsGenerator.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepository, credentialsGenerator, passwordEncoder);
    }

    @Test
    void registerUserShouldSaveUserAndReturnGeneratedCredentials() {
        when(credentialsGenerator.generateUniqueUsername("John", "Smith"))
                .thenReturn("John.Smith");
        when(credentialsGenerator.generatePassword())
                .thenReturn("secret123");
        when(passwordEncoder.encode("secret123"))
                .thenReturn("$2a$encoded");

        User savedUser = mock(User.class);
        UUID userId = UUID.randomUUID();
        when(savedUser.getId()).thenReturn(userId);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        GeneratedCredentials result = userService.registerUser("John", "Smith");

        assertEquals(userId, result.getUserId());
        assertEquals("John.Smith", result.getUsername());
        assertEquals("secret123", result.getPassword());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User passed = captor.getValue();
        assertEquals("John", passed.getFirstName());
        assertEquals("Smith", passed.getLastName());
        assertEquals("John.Smith", passed.getUsername());
        assertEquals("$2a$encoded", passed.getPassword());
        assertTrue(passed.isActive());
    }

    @Test
    void changePasswordShouldUpdatePasswordWhenOldPasswordMatches() {
        User user = new User("John", "Doe", "John.Doe", "$2a$old", true);
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass", "$2a$old")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("$2a$new");

        userService.changePassword("John.Doe", "oldPass", "newPass");

        assertEquals("$2a$new", user.getPassword());
    }

    @Test
    void changePasswordShouldThrowWhenOldPasswordIsWrong() {
        User user = new User("John", "Doe", "John.Doe", "$2a$old", true);
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "$2a$old")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> userService.changePassword("John.Doe", "wrongPass", "newPass"));
    }

    @Test
    void changePasswordShouldThrowWhenUserMissing() {
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.changePassword("John.Doe", "oldPass", "newPass"));
    }

    @Test
    void toggleActiveShouldFlipActiveFlag() {
        User user = new User("John", "Doe", "John.Doe", "pass", true);
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(user));

        userService.toggleActive("John.Doe");
        assertFalse(user.isActive());

        userService.toggleActive("John.Doe");
        assertTrue(user.isActive());
    }

    @Test
    void toggleActiveShouldThrowWhenUserMissing() {
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.toggleActive("John.Doe"));
    }

    @Test
    void deleteUsersShouldDelegateToRepository() {
        Set<UUID> ids = Set.of(UUID.randomUUID(), UUID.randomUUID());

        userService.deleteUsers(ids);

        verify(userRepository).deleteAllById(ids);
    }
}