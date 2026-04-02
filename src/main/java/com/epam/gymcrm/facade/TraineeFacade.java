package com.epam.gymcrm.facade;

import com.epam.gymcrm.dto.response.GeneratedCredentials;
import com.epam.gymcrm.dto.response.TraineeProfileResponse;
import com.epam.gymcrm.dto.response.TraineeTrainingResponse;
import com.epam.gymcrm.dto.response.TrainerSummaryResponse;
import com.epam.gymcrm.entity.Trainee;
import com.epam.gymcrm.entity.Trainer;
import com.epam.gymcrm.entity.Training;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.service.TraineeService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class TraineeFacade {

    private final TraineeService traineeService;

    public TraineeFacade(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    public GeneratedCredentials createTraineeProfile(String firstName,
                                                     String lastName,
                                                     LocalDate dateOfBirth,
                                                     String address) {
        return traineeService.createTraineeProfile(firstName, lastName, dateOfBirth, address);
    }

    public Trainee selectTraineeProfile(String username) {
        return traineeService.selectTraineeProfile(username);
    }

    public TraineeProfileResponse getTraineeProfile(String username) {
        Trainee trainee = selectTraineeProfile(username);
        return mapToResponse(trainee);
    }

    public Trainee updateTraineeAddress(String username, String newAddress) {
        return traineeService.updateTraineeAddress(username, newAddress);
    }

    public void deleteTraineeProfile(String username) {
        traineeService.deleteTraineeProfile(username);
    }

    public long countTrainees() {
        return traineeService.countTrainees();
    }

    public void deleteAllTrainees() {
        traineeService.deleteAllTrainees();
    }

    public List<Training> getTraineeTrainings(String username,
                                              LocalDateTime from,
                                              LocalDateTime to,
                                              String trainerName,
                                              TrainingType type) {
        return traineeService.getTraineeTrainings(username, from, to, trainerName, type);
    }

    public List<Trainer> getNotAssignedTrainers(String username) {
        return traineeService.getNotAssignedTrainers(username);
    }

    public List<TrainerSummaryResponse> updateTraineeTrainers(String username,
                                                              List<String> trainerUsernames) {
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
                                                       String firstName,
                                                       String lastName,
                                                       LocalDate dateOfBirth,
                                                       String address,
                                                       Boolean active) {
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

    public List<TrainerSummaryResponse> getNotAssignedActiveTrainers(String username) {
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
                                                                     LocalDateTime from,
                                                                     LocalDateTime to,
                                                                     String trainerName,
                                                                     TrainingType type) {
        return getTraineeTrainings(username, from, to, trainerName, type)
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