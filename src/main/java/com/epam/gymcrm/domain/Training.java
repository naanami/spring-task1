package com.epam.gymcrm.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Training {
    private final UUID id;
    private final UUID traineeId;
    private final UUID trainerId;
    private final String trainingName;
    private final TrainingType trainingType;
    private final LocalDateTime trainingDate;
    private final int trainingDuration;

    public Training(UUID id, UUID traineeId, UUID trainerId, String trainingName, TrainingType trainingType, LocalDateTime trainingDate, int trainingDuration) {
        this.id = id;
        this.traineeId = traineeId;
        this.trainerId = trainerId;
        this.trainingName = trainingName;
        this.trainingType = trainingType;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
    }

    public UUID getId() {
        return id;
    }
    public UUID getTraineeId() {
        return traineeId;
    }
    public UUID getTrainerId() {
        return trainerId;
    }
    public String getTrainingName() {
        return trainingName;
    }
    public TrainingType getTrainingType() {
        return trainingType;
    }
    public LocalDateTime getTrainingDate() {
        return trainingDate;
    }
    public int getTrainingDuration() {
        return trainingDuration;
    }
}
