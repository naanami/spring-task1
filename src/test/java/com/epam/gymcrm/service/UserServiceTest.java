package com.epam.gymcrm.service;

import com.epam.gymcrm.dao.UserDao;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.dto.GeneratedCredentials;
import com.epam.gymcrm.util.CredentialsGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;
    private UserDao userDao;

    @BeforeEach
    void setup() {
        Map<UUID, User> userStorage = new ConcurrentHashMap<>();

        userDao = new UserDao();
        userDao.setStorage(userStorage);

        CredentialsGenerator generator = new CredentialsGenerator(userDao, 10);

        userService = new UserService();
        userService.setUserDao(userDao);
        userService.setCredentialsGenerator(generator);
    }

    @Test
    void registerUserShouldSaveUserAndReturnCredentials() {
        GeneratedCredentials creds = userService.registerUser("John", "Smith");

        assertNotNull(creds.getUserId());
        assertEquals("John.Smith", creds.getUsername());
        assertNotNull(creds.getPassword());
        assertEquals(10, creds.getPassword().length());

        User saved = userDao.findById(creds.getUserId()).orElseThrow();
        assertEquals("John", saved.getFirstName());
        assertEquals("Smith", saved.getLastName());
        assertEquals("John.Smith", saved.getUsername());
        assertTrue(saved.isActive());
    }

    @Test
    void registerUserShouldGenerateUniqueUsernameIfDuplicate() {
        userService.registerUser("John", "Smith");
        GeneratedCredentials creds2 = userService.registerUser("John", "Smith");

        assertEquals("John.Smith1", creds2.getUsername());
    }
    @Test
    void deleteAllUsersAndCountShouldWork() {
        userService.registerUser("A", "B");
        userService.registerUser("C", "D");

        assertEquals(2, userDao.findAll().size());

        userService.deleteAllUsers();
        assertEquals(0, userDao.findAll().size());

        assertEquals(0, userService.countUsers());
    }

}
