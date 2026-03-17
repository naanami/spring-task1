package com.epam.gymcrm.facade;

import com.epam.gymcrm.dto.GeneratedCredentials;
import com.epam.gymcrm.entity.Trainee;
import com.epam.gymcrm.entity.Trainer;
import com.epam.gymcrm.entity.Training;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.service.AuthService;
import com.epam.gymcrm.service.TraineeService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class TraineeFacade {

    private final TraineeService traineeService;
    private final AuthService authService;

    public TraineeFacade(TraineeService traineeService, AuthService authService) {
        this.traineeService = traineeService;
        this.authService = authService;
    }

    public GeneratedCredentials createTraineeProfile(String firstName,
                                                     String lastName,
                                                     LocalDate dateOfBirth,
                                                     String address) {
        return traineeService.createTraineeProfile(firstName, lastName, dateOfBirth, address);
    }

    public Trainee selectTraineeProfile(String username, String password) {
        authService.authenticate(username, password);
        return traineeService.selectTraineeProfile(username);
    }

    public Trainee updateTraineeAddress(String username, String password, String newAddress) {
        authService.authenticate(username, password);
        return traineeService.updateTraineeAddress(username, newAddress);
    }

    public void deleteTraineeProfile(String username, String password) {
        authService.authenticate(username, password);
        traineeService.deleteTraineeProfile(username);
    }

    public long countTrainees(String username, String password) {
        authService.authenticate(username, password);
        return traineeService.countTrainees();
    }

    public void deleteAllTrainees(String username, String password) {
        authService.authenticate(username, password);
        traineeService.deleteAllTrainees();
    }

    public List<Training> getTraineeTrainings(String username,
                                              String password,
                                              LocalDateTime from,
                                              LocalDateTime to,
                                              String trainerName,
                                              TrainingType type) {
        authService.authenticate(username, password);
        return traineeService.getTraineeTrainings(username, from, to, trainerName, type);
    }

    public List<Trainer> getNotAssignedTrainers(String username, String password) {
        authService.authenticate(username, password);
        return traineeService.getNotAssignedTrainers(username);
    }

    public void updateTraineeTrainers(String username,
                                      String password,
                                      List<String> trainerUsernames) {
        authService.authenticate(username, password);
        traineeService.updateTraineeTrainers(username, trainerUsernames);
    }
}