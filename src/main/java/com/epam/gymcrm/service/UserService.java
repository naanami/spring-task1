package com.epam.gymcrm.service;

import com.epam.gymcrm.dto.GeneratedCredentials;
import com.epam.gymcrm.entity.User;
import com.epam.gymcrm.repository.UserRepository;
import com.epam.gymcrm.util.CredentialsGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final CredentialsGenerator credentialsGenerator;

    public UserService(UserRepository userRepository, CredentialsGenerator credentialsGenerator) {
        this.userRepository = userRepository;
        this.credentialsGenerator = credentialsGenerator;
    }

    @Transactional
    public GeneratedCredentials registerUser(String firstName, String lastName) {
        log.debug("Registering user: firstName={}, lastName={}", firstName, lastName);

        String username = credentialsGenerator.generateUniqueUsername(firstName, lastName);
        String password = credentialsGenerator.generatePassword();

        User user = new User(firstName, lastName, username, password, true);

        User saved = userRepository.save(user);

        log.info("User registered: id={}, username={}", saved.getId(), username);

        return new GeneratedCredentials(saved.getId(), username, password);
    }

    @Transactional
    public void deleteUser(UUID userId) {
        userRepository.deleteById(userId);
    }

    @Transactional
    public void deleteUsers(Set<UUID> ids) {
        userRepository.deleteAllById(ids);
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(oldPassword)) {
            throw new IllegalArgumentException("Invalid password");
        }

        user.setPassword(newPassword);
    }

    @Transactional
    public void toggleActive(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActive(!user.isActive());
    }
}