package com.epam.gymcrm.util;

import com.epam.gymcrm.dao.UserDao;
import com.epam.gymcrm.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class CredentialsGeneratorTest {

    private CredentialsGenerator generator;
    private UserDao userDao;

    @BeforeEach
    void setup() {
        Map<UUID, User> storage = new ConcurrentHashMap<>();
        userDao = new UserDao();
        userDao.setStorage(storage);

        generator = new CredentialsGenerator(userDao, 10);
    }

    @Test
    void shouldGenerateUsernameWhenUnique() {
        String username = generator.generateUniqueUsername("John", "Smith");
        assertEquals("John.Smith", username);
    }

    @Test
    void shouldAddSuffixWhenUsernameExists() {
        UUID id = UUID.randomUUID();
        userDao.save(new User(id, "John", "Smith", "John.Smith", "pass", true));

        String username = generator.generateUniqueUsername("John", "Smith");

        assertEquals("John.Smith1", username);
    }

    @Test
    void shouldGeneratePasswordWithCorrectLength() {
        String password = generator.generatePassword();
        assertEquals(10, password.length());
    }
}
