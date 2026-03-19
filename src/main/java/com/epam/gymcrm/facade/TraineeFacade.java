package com.epam.gymcrm.facade;

import com.epam.gymcrm.dto.response.GeneratedCredentials;
import com.epam.gymcrm.dto.response.TraineeProfileResponse;
import com.epam.gymcrm.dto.response.TraineeTrainingResponse;
import com.epam.gymcrm.dto.response.TrainerSummaryResponse;
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

    public TraineeProfileResponse getTraineeProfile(String username, String password) {
        Trainee trainee = selectTraineeProfile(username, password);
        return mapToResponse(trainee);
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

    public List<TrainerSummaryResponse> updateTraineeTrainers(String username,
                                                              String password,
                                                              List<String> trainerUsernames) {
        authService.authenticate(username, password);
        traineeService.updateTraineeTrainers(username, trainerUsernames);

        Trainee trainee = traineeService.selectTraineeProfile(username);

        return trainee.getTrainers()
                .stream()
                .map(trainer -> new TrainerSummaryResponse(
                        trainer.getUser().getUsername(),
                        trainer.getUser().getFirstName(),
                        trainer.getUser().getLastName(),
                        trainer.getSpecialization()
                ))
                .toList();
    }

    public TraineeProfileResponse updateTraineeProfile(String username,
                                                       String password,
                                                       String firstName,
                                                       String lastName,
                                                       LocalDate dateOfBirth,
                                                       String address,
                                                       Boolean active) {
        authService.authenticate(username, password);

        Trainee trainee = traineeService.updateTraineeProfile(
                username,
                firstName,
                lastName,
                dateOfBirth,
                address,
                active
        );

        return mapToResponse(trainee);
    }

    public List<TrainerSummaryResponse> getNotAssignedActiveTrainers(String username, String password) {
        authService.authenticate(username, password);

        return traineeService.getNotAssignedTrainers(username)
                .stream()
                .map(trainer -> new TrainerSummaryResponse(
                        trainer.getUser().getUsername(),
                        trainer.getUser().getFirstName(),
                        trainer.getUser().getLastName(),
                        trainer.getSpecialization()
                ))
                .toList();
    }

    public List<TraineeTrainingResponse> getTraineeTrainingResponses(String username,
                                                                     String password,
                                                                     LocalDateTime from,
                                                                     LocalDateTime to,
                                                                     String trainerName,
                                                                     TrainingType type) {
        return getTraineeTrainings(username, password, from, to, trainerName, type)
                .stream()
                .map(training -> new TraineeTrainingResponse(
                        training.getTrainingName(),
                        training.getTrainingDate(),
                        training.getTrainingType(),
                        training.getTrainingDuration(),
                        training.getTrainer().getUser().getFirstName() + " " +
                                training.getTrainer().getUser().getLastName()
                ))
                .toList();
    }

    private TraineeProfileResponse mapToResponse(Trainee trainee) {
        List<TrainerSummaryResponse> trainers = trainee.getTrainers()
                .stream()
                .map(trainer -> new TrainerSummaryResponse(
                        trainer.getUser().getUsername(),
                        trainer.getUser().getFirstName(),
                        trainer.getUser().getLastName(),
                        trainer.getSpecialization()
                ))
                .toList();

        return new TraineeProfileResponse(
                trainee.getUser().getUsername(),
                trainee.getUser().getFirstName(),
                trainee.getUser().getLastName(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                trainee.getUser().isActive(),
                trainers
        );
    }
}