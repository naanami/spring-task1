package com.epam.gymcrm.facade;

import com.epam.gymcrm.entity.Training;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.security.SecurityAccessService;
import com.epam.gymcrm.service.TrainingService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class TrainingFacade {

    private final TrainingService trainingService;
    private final SecurityAccessService securityAccessService;

    public TrainingFacade(TrainingService trainingService,
                          SecurityAccessService securityAccessService) {
        this.trainingService = trainingService;
        this.securityAccessService = securityAccessService;
    }

    public void createTraining(String authenticatedUsername,
                               String traineeUsername,
                               String trainerUsername,
                               String trainingName,
                               TrainingType trainingType,
                               LocalDateTime trainingDate,
                               int trainingDuration) {

        securityAccessService.ensureSameUser(authenticatedUsername);

        trainingService.createTraining(
                traineeUsername,
                trainerUsername,
                trainingName,
                trainingType,
                trainingDate,
                trainingDuration
        );
    }

    public Optional<Training> selectTraining(UUID trainingId) {
        return trainingService.selectTraining(trainingId);
    }

    public List<Training> selectAllTrainings() {
        return trainingService.selectAllTrainings();
    }

    public long countTrainings() {
        return trainingService.countTrainings();
    }

    public void deleteAllTrainings() {
        trainingService.deleteAllTrainings();
    }
}