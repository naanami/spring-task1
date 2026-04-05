package com.epam.gymcrm.service;

import com.epam.gymcrm.entity.User;
import com.epam.gymcrm.exception.NotFoundException;
import com.epam.gymcrm.exception.UserBlockedException;
import com.epam.gymcrm.repository.UserRepository;
import com.epam.gymcrm.security.LoginAttemptService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       LoginAttemptService loginAttemptService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.loginAttemptService = loginAttemptService;
    }

    public User authenticate(String username, String password) {

        if (loginAttemptService.isBlocked(username)) {
            long remainingSeconds = loginAttemptService.getRemainingBlockSeconds(username);
            throw new UserBlockedException(
                    "User is blocked due to too many failed attempts. Try again in "
                            + remainingSeconds + " seconds."
            );
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
        } catch (BadCredentialsException e) {
            loginAttemptService.loginFailed(username);

            if (loginAttemptService.isBlocked(username)) {
                long remainingSeconds = loginAttemptService.getRemainingBlockSeconds(username);
                throw new UserBlockedException(
                        "User is blocked due to too many failed attempts. Try again in "
                                + remainingSeconds + " seconds."
                );
            }

            throw new IllegalArgumentException("Invalid credentials");
        } catch (DisabledException e) {
            throw new IllegalArgumentException("User is inactive");
        }

        loginAttemptService.loginSucceeded(username);

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));
    }
}