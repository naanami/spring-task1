package com.epam.gymcrm.service;

import com.epam.gymcrm.dto.response.GeneratedCredentials;
import com.epam.gymcrm.entity.User;
import com.epam.gymcrm.exception.NotFoundException;
import com.epam.gymcrm.repository.UserRepository;
import com.epam.gymcrm.util.CredentialsGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final CredentialsGenerator credentialsGenerator;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       CredentialsGenerator credentialsGenerator,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.credentialsGenerator = credentialsGenerator;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public GeneratedCredentials registerUser(String firstName, String lastName) {
        log.debug("Registering user: firstName={}, lastName={}", firstName, lastName);

        String username = credentialsGenerator.generateUniqueUsername(firstName, lastName);
        String rawPassword = credentialsGenerator.generatePassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        User user = new User(firstName, lastName, username, encodedPassword, true);
        User saved = userRepository.save(user);

        log.info("User registered: id={}, username={}", saved.getId(), username);

        return new GeneratedCredentials(saved.getId(), username, rawPassword);
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
                .orElseThrow(() -> new NotFoundException("User not found: " + username));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
    }

    @Transactional
    public void toggleActive(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));

        user.setActive(!user.isActive());
    }
}