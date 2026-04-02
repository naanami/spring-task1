package com.epam.gymcrm.service;

import com.epam.gymcrm.entity.User;
import com.epam.gymcrm.exception.NotFoundException;
import com.epam.gymcrm.exception.UserBlockedException;
import com.epam.gymcrm.repository.UserRepository;
import com.epam.gymcrm.security.LoginAttemptService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
    }

    public User authenticate(String username, String password) {

        if (loginAttemptService.isBlocked(username)) {
            throw new UserBlockedException("User is blocked due to too many failed attempts");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            loginAttemptService.loginFailed(username);
            throw new IllegalArgumentException("Invalid credentials");
        }

        loginAttemptService.loginSucceeded(username);

        return user;
    }
}