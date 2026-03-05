package com.epam.gymcrm.service;

import com.epam.gymcrm.dto.GeneratedCredentials;
import com.epam.gymcrm.entity.*;
import com.epam.gymcrm.exception.NotFoundException;
import com.epam.gymcrm.repository.TraineeRepository;
import com.epam.gymcrm.repository.TrainerRepository;
import com.epam.gymcrm.repository.TrainingRepository;
import com.epam.gymcrm.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TraineeService {

    private static final Logger log = LoggerFactory.getLogger(TraineeService.class);

    private final TraineeRepository traineeRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;

    public TraineeService(TraineeRepository traineeRepository,
                          UserRepository userRepository,
                          UserService userService,
                          TrainerRepository trainerRepository,
                          TrainingRepository trainingRepository) {

        this.traineeRepository = traineeRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.trainerRepository = trainerRepository;
        this.trainingRepository = trainingRepository;
    }

    @Transactional
    public GeneratedCredentials createTraineeProfile(String firstName,
                                                     String lastName,
                                                     LocalDate dateOfBirth,
                                                     String address) {

        log.debug("Creating trainee profile for {} {}", firstName, lastName);

        GeneratedCredentials creds = userService.registerUser(firstName, lastName);

        User user = userRepository.findById(creds.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found: " + creds.getUserId()));

        Trainee trainee = new Trainee(user, dateOfBirth, address);
        traineeRepository.save(trainee);

        log.info("Trainee profile created: userId={}", creds.getUserId());
        return creds;
    }

    @Transactional(readOnly = true)
    public Optional<Trainee> selectTraineeProfile(UUID userId) {
        log.debug("Selecting trainee profile: userId={}", userId);
        return traineeRepository.findByUserId(userId);
    }

    @Transactional
    public Trainee updateTraineeAddress(UUID userId, String newAddress) {
        log.debug("Updating trainee address: userId={}", userId);

        Trainee trainee = traineeRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("Trainee not found: {}", userId);
                    return new NotFoundException("Trainee not found for userId: " + userId);
                });

        trainee.setAddress(newAddress);
        Trainee saved = traineeRepository.save(trainee);

        log.info("Trainee address updated: userId={}", userId);
        return saved;
    }

    @Transactional
    public void deleteTraineeProfile(UUID userId) {
        log.info("Deleting trainee profile: userId={}", userId);

        Trainee trainee = traineeRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Trainee not found for userId: " + userId));

        trainee.getTrainers().forEach(tr -> tr.getTrainees().remove(trainee));
        trainee.getTrainers().clear();
        traineeRepository.delete(trainee);
        userService.deleteUser(userId);
    }

    @Transactional(readOnly = true)
    public long countTrainees() {
        return traineeRepository.count();
    }

    @Transactional
    public void deleteAllTrainees() {
        var userIds = traineeRepository.findAll().stream()
                .map(t -> t.getUser().getId())
                .collect(Collectors.toSet());

        traineeRepository.deleteAll();
        userService.deleteUsers(userIds);
    }

    @Transactional(readOnly = true)
    public List<Training> getTraineeTrainings(String username,
                                              LocalDateTime from,
                                              LocalDateTime to,
                                              String trainerName,
                                              TrainingType type) {

        return trainingRepository.findTraineeTrainings(
                username,
                from,
                to,
                trainerName,
                type
        );
    }

    @Transactional(readOnly = true)
    public List<Trainer> getNotAssignedTrainers(String traineeUsername) {

        return trainerRepository.findNotAssignedToTrainee(traineeUsername);
    }

    @Transactional
    public void updateTraineeTrainers(String traineeUsername, List<String> trainerUsernames) {

        Trainee trainee = traineeRepository.findByUserUsername(traineeUsername)
                .orElseThrow(() -> new NotFoundException("Trainee not found"));

        trainee.getTrainers().clear();

        for (String trainerUsername : trainerUsernames) {
            Trainer trainer = trainerRepository.findByUserUsername(trainerUsername)
                    .orElseThrow(() -> new NotFoundException("Trainer not found: " + trainerUsername));

            trainee.addTrainer(trainer);
        }
    }
}