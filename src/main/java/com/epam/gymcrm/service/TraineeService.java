package com.epam.gymcrm.service;

import com.epam.gymcrm.dto.response.GeneratedCredentials;
import com.epam.gymcrm.entity.Trainee;
import com.epam.gymcrm.entity.Trainer;
import com.epam.gymcrm.entity.Training;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.entity.User;
import com.epam.gymcrm.exception.NotFoundException;
import com.epam.gymcrm.repository.TraineeRepository;
import com.epam.gymcrm.repository.TrainerRepository;
import com.epam.gymcrm.repository.TrainingRepository;
import com.epam.gymcrm.repository.UserRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class TraineeService {

    private static final Logger log = LoggerFactory.getLogger(TraineeService.class);

    private final TraineeRepository traineeRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    private final TrainerWorkloadIntegrationService trainerWorkloadIntegrationService;

    public TraineeService(TraineeRepository traineeRepository,
                          UserRepository userRepository,
                          UserService userService,
                          TrainerRepository trainerRepository,
                          TrainingRepository trainingRepository,
                          TrainerWorkloadIntegrationService trainerWorkloadIntegrationService,
                          MeterRegistry meterRegistry) {
        this.traineeRepository = traineeRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.trainerRepository = trainerRepository;
        this.trainingRepository = trainingRepository;
        this.trainerWorkloadIntegrationService = trainerWorkloadIntegrationService;

        meterRegistry.gauge("trainees.count", this, service -> service.countTrainees());
    }

    @Transactional
    public GeneratedCredentials createTraineeProfile(String firstName,
                                                     String lastName,
                                                     LocalDate dateOfBirth,
                                                     String address) {
        log.debug("Creating trainee profile for {} {}", firstName, lastName);

        if (trainerRepository.existsByUserFirstNameAndUserLastName(firstName, lastName)) {
            log.warn("Cannot create trainee profile because trainer profile already exists for {} {}", firstName, lastName);
            throw new IllegalArgumentException("User cannot be registered as both trainer and trainee");
        }

        GeneratedCredentials credentials = userService.registerUser(firstName, lastName);

        User user = userRepository.findById(credentials.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found: " + credentials.getUserId()));

        Trainee trainee = new Trainee(user, dateOfBirth, address);
        traineeRepository.save(trainee);

        log.info("Trainee profile created: userId={}", credentials.getUserId());
        return credentials;
    }

    @Transactional(readOnly = true)
    public Trainee selectTraineeProfile(String username) {
        log.debug("Selecting trainee profile: username={}", username);

        return traineeRepository.findDetailedByUserUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainee not found for username: " + username));
    }

    @Transactional
    public Trainee updateTraineeAddress(String username, String newAddress) {
        log.debug("Updating trainee address: username={}", username);

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainee not found: {}", username);
                    return new NotFoundException("Trainee not found for username: " + username);
                });

        trainee.setAddress(newAddress);
        Trainee savedTrainee = traineeRepository.save(trainee);

        log.info("Trainee address updated: username={}", username);
        return savedTrainee;
    }

    @Transactional
    public void deleteTraineeProfile(String username) {
        log.info("Deleting trainee profile: username={}", username);

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainee not found for username: " + username));

        List<Training> trainings = trainingRepository.findByTrainee_User_Username(username);

        for (Training training : trainings) {
            trainerWorkloadIntegrationService.sendTrainingDeleted(
                    training.getTrainer(),
                    training.getTrainingDate().toLocalDate(),
                    training.getTrainingDuration()
            );
        }

        for (Trainer existingTrainer : List.copyOf(trainee.getTrainers())) {
            trainee.removeTrainer(existingTrainer);
        }

        traineeRepository.delete(trainee);
        userService.deleteUser(trainee.getUser().getId());

        log.info("Trainee profile deleted: username={}, deletedTrainings={}", username, trainings.size());
    }

    @Transactional(readOnly = true)
    public long countTrainees() {
        return traineeRepository.count();
    }

    @Transactional
    public void deleteAllTrainees() {
        Set<java.util.UUID> userIds = traineeRepository.findAll().stream()
                .map(trainee -> trainee.getUser().getId())
                .collect(java.util.stream.Collectors.toSet());

        traineeRepository.deleteAll();
        userService.deleteUsers(userIds);
    }

    @Transactional(readOnly = true)
    public List<Training> getTraineeTrainings(String username,
                                              LocalDateTime from,
                                              LocalDateTime to,
                                              String trainerName,
                                              TrainingType trainingType) {
        return trainingRepository.findTraineeTrainings(username, from, to, trainerName, trainingType);
    }

    @Transactional(readOnly = true)
    public List<Trainer> getNotAssignedTrainers(String username) {
        selectTraineeProfile(username);
        return trainerRepository.findActiveTrainersNotAssignedToTrainee(username);
    }

    @Transactional
    public void updateTraineeTrainers(String username, List<String> trainerUsernames) {
        Trainee trainee = traineeRepository.findDetailedByUserUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainee not found for username: " + username));

        List<Trainer> newTrainers = trainerRepository.findByUserUsernameIn(trainerUsernames);

        trainee.getTrainers().clear();
        trainee.getTrainers().addAll(newTrainers);

        log.info("Trainee trainers updated: username={}, trainersCount={}", username, newTrainers.size());
    }

    @Transactional
    public Trainee updateTraineeProfile(String username,
                                        String firstName,
                                        String lastName,
                                        LocalDate dateOfBirth,
                                        String address,
                                        Boolean active) {
        log.debug("Updating trainee profile: username={}", username);

        Trainee trainee = traineeRepository.findDetailedByUserUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainee not found for username: " + username));

        User user = trainee.getUser();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setActive(active);

        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);

        log.info("Trainee profile updated: username={}", username);
        return trainee;
    }
}