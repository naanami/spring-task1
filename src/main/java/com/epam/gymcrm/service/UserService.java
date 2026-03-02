package com.epam.gymcrm.service;

import com.epam.gymcrm.dao.UserDao;
import com.epam.gymcrm.entity.User;
import com.epam.gymcrm.dto.GeneratedCredentials;
import com.epam.gymcrm.util.CredentialsGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private UserDao userDao;
    private CredentialsGenerator credentialsGenerator;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setCredentialsGenerator(CredentialsGenerator credentialsGenerator) {
        this.credentialsGenerator = credentialsGenerator;
    }

     GeneratedCredentials registerUser(String firstName, String lastName) {
        log.debug("Registering user: firstName={}, lastName={}", firstName, lastName);

        String username = credentialsGenerator.generateUniqueUsername(firstName, lastName);
        String password = credentialsGenerator.generatePassword();

        UUID userId = UUID.randomUUID();

        User user = new User(
                userId,
                firstName,
                lastName,
                username,
                password,
                true
        );

        userDao.save(user);

        log.info("User registered: id={}, username={}", userId, username);

        return new GeneratedCredentials(userId, username, password);
    }

    void deleteUser(UUID userId) {
        userDao.deleteById(userId);
    }

    void deleteUsers(Set<UUID> ids) {
        for (UUID id : ids) {
            userDao.deleteById(id);
        }
    }


}
