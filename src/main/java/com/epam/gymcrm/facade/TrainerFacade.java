package com.epam.gymcrm.facade;

import com.epam.gymcrm.dto.response.GeneratedCredentials;
import com.epam.gymcrm.dto.response.TraineeSummaryResponse;
import com.epam.gymcrm.dto.response.TrainerProfileResponse;
import com.epam.gymcrm.dto.response.TrainerTrainingResponse;
import com.epam.gymcrm.entity.Trainer;
import com.epam.gymcrm.entity.Training;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.service.TrainerService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TrainerFacade {

    private final TrainerService trainerService;

    public TrainerFacade(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    public GeneratedCredentials createTrainerProfile(String firstName,
                                                     String lastName,
                                                     TrainingType specialization) {
        return trainerService.createTrainerProfile(firstName, lastName, specialization);
    }

    public TrainerProfileResponse getTrainerProfile(String username) {
        Trainer trainer = trainerService.selectTrainerProfile(username);
        return mapToResponse(trainer);
    }

    public TrainerProfileResponse updateTrainerProfile(String username,
                                                       String firstName,
                                                       String lastName,
                                                       Boolean active) {
        Trainer trainer = trainerService.updateTrainerProfile(
                username,
                firstName,
                lastName,
                active
        );

        return mapToResponse(trainer);
    }

    public List<Training> getTrainerTrainings(String username,
                                              LocalDateTime from,
                                              LocalDateTime to,
                                              String traineeName) {
        return trainerService.getTrainerTrainings(username, from, to, traineeName);
    }

    public List<TrainerTrainingResponse> getTrainerTrainingResponses(String username,
                                                                     LocalDateTime from,
                                                                     LocalDateTime to,
                                                                     String traineeName) {
        return getTrainerTrainings(username, from, to, traineeName)
                .stream()
                .map(training -> new TrainerTrainingResponse(
                        training.getTrainingName(),
                        training.getTrainingDate(),
                        training.getTrainingType(),
                        training.getTrainingDuration(),
                        training.getTrainee().getUser().getFirstName() + " " +
                                training.getTrainee().getUser().getLastName()
                ))
                .toList();
    }

    private TrainerProfileResponse mapToResponse(Trainer trainer) {
        List<TraineeSummaryResponse> trainees = trainer.getTrainees()
                .stream()
                .map(trainee -> new TraineeSummaryResponse(
                        trainee.getUser().getUsername(),
                        trainee.getUser().getFirstName(),
                        trainee.getUser().getLastName()
                ))
                .toList();

        return new TrainerProfileResponse(
                trainer.getUser().getUsername(),
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getSpecialization(),
                trainer.getUser().isActive(),
                trainees
        );
    }
}