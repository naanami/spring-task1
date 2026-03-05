package com.epam.gymcrm.facade;

import com.epam.gymcrm.entity.Training;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.service.AuthService;
import com.epam.gymcrm.service.TrainingService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class TrainingFacade {

    private final TrainingService trainingService;
    private final AuthService authService;

    public TrainingFacade(TrainingService trainingService, AuthService authService) {
        this.trainingService = trainingService;
        this.authService = authService;
    }

    public Training createTraining(String username,
                                   String password,
                                   UUID traineeId,
                                   UUID trainerId,
                                   String trainingName,
                                   TrainingType trainingType,
                                   LocalDateTime trainingDate,
                                   int trainingDuration) {

        authService.authenticate(username, password);

        return trainingService.createTraining(
                traineeId,
                trainerId,
                trainingName,
                trainingType,
                trainingDate,
                trainingDuration
        );
    }

    public Optional<Training> selectTraining(String username, String password, UUID trainingId) {
        authService.authenticate(username, password);
        return trainingService.selectTraining(trainingId);
    }

    public List<Training> selectAllTrainings(String username, String password) {
        authService.authenticate(username, password);
        return trainingService.selectAllTrainings();
    }

    public long countTrainings(String username, String password) {
        authService.authenticate(username, password);
        return trainingService.countTrainings();
    }

    public void deleteAllTrainings(String username, String password) {
        authService.authenticate(username, password);
        trainingService.deleteAllTrainings();
    }
}