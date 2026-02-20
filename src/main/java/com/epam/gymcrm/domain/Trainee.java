package com.epam.gymcrm.domain;

import java.time.LocalDate;
import java.util.UUID;

public class Trainee {
    private final UUID userId;
    private final LocalDate dateOfBirth;
    private String address;

    public Trainee(UUID userId, LocalDate dateOfBirth, String address) {
        this.userId = userId;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }
    public UUID getUserId() {
        return userId;
    }
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
}

