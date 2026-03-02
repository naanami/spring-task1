package com.epam.gymcrm.entity;

import java.util.UUID;

public class User {
    private final UUID id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean active;

    public User(UUID id, String firstName, String lastName, String username, String password, boolean active) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.active = active;
    }

    public UUID getId() {
        return id;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
}
