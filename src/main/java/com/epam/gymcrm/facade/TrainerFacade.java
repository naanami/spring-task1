package com.epam.gymcrm.facade;

import com.epam.gymcrm.dto.GeneratedCredentials;
import com.epam.gymcrm.entity.Trainer;
import com.epam.gymcrm.entity.Training;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.service.AuthService;
import com.epam.gymcrm.service.TrainerService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TrainerFacade {

    private final TrainerService trainerService;
    private final AuthService authService;

    public TrainerFacade(TrainerService trainerService, AuthService authService) {
        this.trainerService = trainerService;
        this.authService = authService;
    }

    public GeneratedCredentials createTrainerProfile(String firstName,
                                                     String lastName,
                                                     TrainingType specialization) {
        return trainerService.createTrainerProfile(firstName, lastName, specialization);
    }

    public Trainer selectTrainerProfile(String username, String password) {
        authService.authenticate(username, password);
        return trainerService.selectTrainerProfile(username);
    }

    public Trainer updateTrainerProfile(String username,
                                        String password,
                                        TrainingType newSpecialization) {
        authService.authenticate(username, password);
        return trainerService.updateTrainerProfile(username, newSpecialization);
    }

    public long countTrainers(String username, String password) {
        authService.authenticate(username, password);
        return trainerService.countTrainers();
    }

    public void deleteAllTrainers(String username, String password) {
        authService.authenticate(username, password);
        trainerService.deleteAllTrainers();
    }

    public List<Training> getTrainerTrainings(String username,
                                              String password,
                                              LocalDateTime from,
                                              LocalDateTime to,
                                              String traineeName) {
        authService.authenticate(username, password);
        return trainerService.getTrainerTrainings(username, from, to, traineeName);
    }
}