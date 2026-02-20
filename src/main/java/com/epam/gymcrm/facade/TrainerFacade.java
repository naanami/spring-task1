package com.epam.gymcrm.facade;

import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.dto.GeneratedCredentials;
import com.epam.gymcrm.service.TrainerService;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class TrainerFacade {
    private final TrainerService trainerService;

    public TrainerFacade(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    public GeneratedCredentials createTrainerProfile(String firstName, String lastName, TrainingType specialization) {
        return trainerService.createTrainerProfile(firstName, lastName, specialization);
    }
    public Optional<Trainer> selectTrainerProfile(UUID userId) {
        return trainerService.selectTrainerProfile(userId);
    }
    public Trainer updateTrainerProfile(UUID userId, TrainingType newSpecialization) {
        return trainerService.updateTrainerProfile(userId, newSpecialization);
    }
    public long countTrainers() {
        return trainerService.countTrainers();
    }

    public void deleteAllTrainers() {
        trainerService.deleteAllTrainers();
    }

}
