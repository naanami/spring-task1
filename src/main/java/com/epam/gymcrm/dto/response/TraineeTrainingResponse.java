package com.epam.gymcrm.dto.response;

import com.epam.gymcrm.entity.TrainingType;

import java.time.LocalDateTime;

public class TraineeTrainingResponse {

    private String trainingName;
    private LocalDateTime trainingDate;
    private TrainingType trainingType;
    private int trainingDuration;
    private String trainerName;

    public TraineeTrainingResponse(String trainingName,
                                   LocalDateTime trainingDate,
                                   TrainingType trainingType,
                                   int trainingDuration,
                                   String trainerName) {
        this.trainingName = trainingName;
        this.trainingDate = trainingDate;
        this.trainingType = trainingType;
        this.trainingDuration = trainingDuration;
        this.trainerName = trainerName;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public LocalDateTime getTrainingDate() {
        return trainingDate;
    }

    public TrainingType getTrainingType() {
        return trainingType;
    }

    public int getTrainingDuration() {
        return trainingDuration;
    }

    public String getTrainerName() {
        return trainerName;
    }
}