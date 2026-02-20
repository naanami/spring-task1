package com.epam.gymcrm.facade;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.dto.GeneratedCredentials;
import com.epam.gymcrm.service.TraineeService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Component
public class TraineeFacade {

    private final TraineeService traineeService;

    public TraineeFacade(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    public GeneratedCredentials createTraineeProfile(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        return traineeService.createTraineeProfile(firstName, lastName, dateOfBirth, address);
    }

    public Optional<Trainee> selectTraineeProfile(UUID userId) {
        return traineeService.selectTraineeProfile(userId);
    }

    public Trainee updateTraineeAddress(UUID userId, String newAddress) {
        return traineeService.updateTraineeAddress(userId, newAddress);
    }

    public void deleteTraineeProfile(UUID userId) {
        traineeService.deleteTraineeProfile(userId);
    }
    public long countTrainees() {
        return traineeService.countTrainees();
    }

    public void deleteAllTrainees() {
        traineeService.deleteAllTrainees();
    }

}
