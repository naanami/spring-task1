package com.epam.gymcrm.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private final int maxAttempts;
    private final long blockDurationMinutes;
    private final long blockDurationSeconds;

    private final Map<String, Integer> attempts = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> blockedUntil = new ConcurrentHashMap<>();

    public LoginAttemptService(
            @Value("${app.security.max-attempts}") int maxAttempts,
            @Value("${app.security.block-duration-minutes:0}") long blockDurationMinutes,
            @Value("${app.security.block-duration-seconds:0}") long blockDurationSeconds
    ) {
        this.maxAttempts = maxAttempts;
        this.blockDurationMinutes = blockDurationMinutes;
        this.blockDurationSeconds = blockDurationSeconds;
    }

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

        if (newAttempts >= maxAttempts) {
            blockedUntil.put(username, LocalDateTime.now().plus(getBlockDuration()));
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

        if (blockEnd == null || !isBlocked(username)) {
            return 0;
        }

        return Duration.between(LocalDateTime.now(), blockEnd).getSeconds();
    }

    private int getAttempts(String username) {
        return attempts.getOrDefault(username, 0);
    }

    private Duration getBlockDuration() {
        if (blockDurationSeconds > 0) {
            return Duration.ofSeconds(blockDurationSeconds);
        }
        return Duration.ofMinutes(blockDurationMinutes);
    }
}