package com.epam.gymcrm.domain;

import java.util.UUID;

public class Trainer {
    private final TrainingType specialization;
    private final UUID userId;

    public Trainer(UUID userId, TrainingType specialization){
        this.userId = userId;
        this.specialization = specialization;
    }

    public UUID getUserId() {
        return userId;
    }

    public TrainingType getSpecialization() {
        return specialization;
    }
}
