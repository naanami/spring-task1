package com.epam.gymcrm.service;

import com.epam.gymcrm.dto.response.GeneratedCredentials;
import com.epam.gymcrm.entity.User;
import com.epam.gymcrm.repository.UserRepository;
import com.epam.gymcrm.util.CredentialsGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private CredentialsGenerator credentialsGenerator;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        credentialsGenerator = mock(CredentialsGenerator.class);
        userService = new UserService(userRepository, credentialsGenerator);
    }

    @Test
    void registerUserShouldSaveUserAndReturnGeneratedCredentials() {
        when(credentialsGenerator.generateUniqueUsername("John", "Smith"))
                .thenReturn("John.Smith");
        when(credentialsGenerator.generatePassword())
                .thenReturn("secret123");

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
        assertEquals("secret123", passed.getPassword());
        assertTrue(passed.isActive());
    }

    @Test
    void changePasswordShouldUpdatePasswordWhenOldPasswordMatches() {
        User user = new User("John", "Doe", "John.Doe", "oldPass", true);
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(user));

        userService.changePassword("John.Doe", "oldPass", "newPass");

        assertEquals("newPass", user.getPassword());
    }

    @Test
    void changePasswordShouldThrowWhenOldPasswordIsWrong() {
        User user = new User("John", "Doe", "John.Doe", "oldPass", true);
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> userService.changePassword("John.Doe", "wrongPass", "newPass"));
    }

    @Test
    void activateUserShouldSetActiveTrue() {
        User user = new User("John", "Doe", "John.Doe", "pass", false);
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(user));

        userService.activateUser("John.Doe");

        assertTrue(user.isActive());
    }

    @Test
    void deactivateUserShouldSetActiveFalse() {
        User user = new User("John", "Doe", "John.Doe", "pass", true);
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(user));

        userService.deactivateUser("John.Doe");

        assertFalse(user.isActive());
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
    void deleteUsersShouldDelegateToRepository() {
        Set<UUID> ids = Set.of(UUID.randomUUID(), UUID.randomUUID());

        userService.deleteUsers(ids);

        verify(userRepository).deleteAllById(ids);
    }
}