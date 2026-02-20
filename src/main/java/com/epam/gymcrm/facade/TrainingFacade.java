package com.epam.gymcrm.facade;

import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.service.TrainingService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class TrainingFacade {

    private final TrainingService trainingService;

    public TrainingFacade(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    public Training createTraining(UUID traineeId,
                                   UUID trainerId,
                                   String trainingName,
                                   TrainingType trainingType,
                                   LocalDateTime trainingDate,
                                   int trainingDuration) {
        return trainingService.createTraining(traineeId, trainerId, trainingName, trainingType, trainingDate, trainingDuration);
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
