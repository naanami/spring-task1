package com.epam.gymcrm.facade;

import com.epam.gymcrm.dto.GeneratedCredentials;
import com.epam.gymcrm.entity.Trainer;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.service.AuthService;
import com.epam.gymcrm.service.TrainerService;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class TrainerFacade {

    private final TrainerService trainerService;
    private final AuthService authService;

    public TrainerFacade(TrainerService trainerService, AuthService authService) {
        this.trainerService = trainerService;
        this.authService = authService;
    }

    // No authentication required
    public GeneratedCredentials createTrainerProfile(
            String firstName,
            String lastName,
            TrainingType specialization
    ) {
        return trainerService.createTrainerProfile(firstName, lastName, specialization);
    }

    public Optional<Trainer> selectTrainerProfile(String username, String password, UUID userId) {
        authService.authenticate(username, password);
        return trainerService.selectTrainerProfile(userId);
    }

    public Trainer updateTrainerProfile(String username,
                                        String password,
                                        UUID userId,
                                        TrainingType newSpecialization) {
        authService.authenticate(username, password);
        return trainerService.updateTrainerProfile(userId, newSpecialization);
    }

    public long countTrainers(String username, String password) {
        authService.authenticate(username, password);
        return trainerService.countTrainers();
    }

    public void deleteAllTrainers(String username, String password) {
        authService.authenticate(username, password);
        trainerService.deleteAllTrainers();
    }
}