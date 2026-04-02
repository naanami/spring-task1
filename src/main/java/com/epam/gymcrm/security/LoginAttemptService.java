package com.epam.gymcrm.security;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 3;

    private final Map<String, Integer> attempts = new ConcurrentHashMap<>();

    public void loginSucceeded(String username) {
        attempts.remove(username);
    }

    public void loginFailed(String username) {
        attempts.put(username, getAttempts(username) + 1);
    }

    public boolean isBlocked(String username) {
        return getAttempts(username) >= MAX_ATTEMPTS;
    }

    private int getAttempts(String username) {
        return attempts.getOrDefault(username, 0);
    }
}