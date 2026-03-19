package com.epam.gymcrm.dto.response;

import com.epam.gymcrm.entity.TrainingType;

public class TrainerSummaryResponse {

    private String username;
    private String firstName;
    private String lastName;
    private TrainingType specialization;

    public TrainerSummaryResponse(String username,
                                  String firstName,
                                  String lastName,
                                  TrainingType specialization) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public TrainingType getSpecialization() {
        return specialization;
    }
}