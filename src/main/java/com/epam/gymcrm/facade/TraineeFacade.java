package com.epam.gymcrm.facade;

import com.epam.gymcrm.dto.GeneratedCredentials;
import com.epam.gymcrm.entity.Trainee;
import com.epam.gymcrm.service.AuthService;
import com.epam.gymcrm.service.TraineeService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Component
public class TraineeFacade {

    private final TraineeService traineeService;
    private final AuthService authService;

    public TraineeFacade(TraineeService traineeService, AuthService authService) {
        this.traineeService = traineeService;
        this.authService = authService;
    }

    // No authentication required
    public GeneratedCredentials createTraineeProfile(
            String firstName,
            String lastName,
            LocalDate dateOfBirth,
            String address
    ) {
        return traineeService.createTraineeProfile(firstName, lastName, dateOfBirth, address);
    }

    public Optional<Trainee> selectTraineeProfile(String username, String password, UUID userId) {
        authService.authenticate(username, password);
        return traineeService.selectTraineeProfile(userId);
    }

    public Trainee updateTraineeAddress(String username, String password, UUID userId, String newAddress) {
        authService.authenticate(username, password);
        return traineeService.updateTraineeAddress(userId, newAddress);
    }

    public void deleteTraineeProfile(String username, String password, UUID userId) {
        authService.authenticate(username, password);
        traineeService.deleteTraineeProfile(userId);
    }

    public long countTrainees(String username, String password) {
        authService.authenticate(username, password);
        return traineeService.countTrainees();
    }

    public void deleteAllTrainees(String username, String password) {
        authService.authenticate(username, password);
        traineeService.deleteAllTrainees();
    }
}