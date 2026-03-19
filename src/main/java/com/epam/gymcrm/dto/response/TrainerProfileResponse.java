package com.epam.gymcrm.dto.response;

import com.epam.gymcrm.entity.TrainingType;

import java.util.List;

public class TrainerProfileResponse {

    private String username;
    private String firstName;
    private String lastName;
    private TrainingType specialization;
    private boolean active;
    private List<TraineeSummaryResponse> trainees;

    public TrainerProfileResponse(String username,
                                  String firstName,
                                  String lastName,
                                  TrainingType specialization,
                                  boolean active,
                                  List<TraineeSummaryResponse> trainees) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.active = active;
        this.trainees = trainees;
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

    public boolean isActive() {
        return active;
    }

    public List<TraineeSummaryResponse> getTrainees() {
        return trainees;
    }
}