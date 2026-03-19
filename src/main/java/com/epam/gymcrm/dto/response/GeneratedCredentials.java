package com.epam.gymcrm.dto.response;

import java.util.UUID;

public class GeneratedCredentials {
    private final UUID userId;
    private final String username;
    private final String password;

    public GeneratedCredentials(UUID userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }
    public UUID getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
