package com.epam.gymcrm.security;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 3;
    private static final long BLOCK_DURATION_MINUTES = 5;

    private final Map<String, Integer> attempts = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> blockedUntil = new ConcurrentHashMap<>();

    public void loginSucceeded(String username) {
        attempts.remove(username);
        blockedUntil.remove(username);
    }

    public void loginFailed(String username) {
        if (isBlocked(username)) {
            return;
        }

        int newAttempts = getAttempts(username) + 1;
        attempts.put(username, newAttempts);

        if (newAttempts >= MAX_ATTEMPTS) {
            blockedUntil.put(username, LocalDateTime.now().plusMinutes(BLOCK_DURATION_MINUTES));
        }
    }

    public boolean isBlocked(String username) {
        LocalDateTime blockEnd = blockedUntil.get(username);

        if (blockEnd == null) {
            return false;
        }

        if (LocalDateTime.now().isAfter(blockEnd)) {
            attempts.remove(username);
            blockedUntil.remove(username);
            return false;
        }

        return true;
    }

    public long getRemainingBlockSeconds(String username) {
        LocalDateTime blockEnd = blockedUntil.get(username);

        if (blockEnd == null) {
            return 0;
        }

        if (!isBlocked(username)) {
            return 0;
        }

        return java.time.Duration.between(LocalDateTime.now(), blockEnd).getSeconds();
    }

    private int getAttempts(String username) {
        return attempts.getOrDefault(username, 0);
    }
}