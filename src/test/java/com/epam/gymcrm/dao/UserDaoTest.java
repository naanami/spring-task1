package com.epam.gymcrm.dao;

import com.epam.gymcrm.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class UserDaoTest {

    private UserDao userDao;

    @BeforeEach
    void setup() {
        Map<UUID, User> storage = new ConcurrentHashMap<>();
        userDao = new UserDao();
        userDao.setStorage(storage);
    }

    @Test
    void saveAndFindById() {
        UUID id = UUID.randomUUID();
        User u = new User(id, "A", "B", "A.B", "p", true);

        userDao.save(u);

        assertTrue(userDao.findById(id).isPresent());
        assertEquals("A.B", userDao.findById(id).get().getUsername());
    }

    @Test
    void findAllReturnsAll() {
        userDao.save(new User(UUID.randomUUID(), "A", "B", "A.B", "p", true));
        userDao.save(new User(UUID.randomUUID(), "C", "D", "C.D", "p", true));

        assertEquals(2, userDao.findAll().size());
    }

    @Test
    void deleteByIdRemovesUser() {
        UUID id = UUID.randomUUID();
        userDao.save(new User(id, "A", "B", "A.B", "p", true));

        userDao.deleteById(id);

        assertTrue(userDao.findById(id).isEmpty());
    }

    @Test
    void existsByUsernameWorks() {
        userDao.save(new User(UUID.randomUUID(), "A", "B", "A.B", "p", true));

        assertTrue(userDao.existsByUsername("A.B"));
        assertFalse(userDao.existsByUsername("X.Y"));
    }

    @Test
    void findByUsernamePrefixWorks() {
        userDao.save(new User(UUID.randomUUID(), "A", "B", "John.Doe", "p", true));
        userDao.save(new User(UUID.randomUUID(), "C", "D", "John.Smith", "p", true));
        userDao.save(new User(UUID.randomUUID(), "E", "F", "Anna.Brown", "p", true));

        assertEquals(2, userDao.findByUsernamePrefix("John").size());
        assertEquals(1, userDao.findByUsernamePrefix("Anna").size());
    }
}
