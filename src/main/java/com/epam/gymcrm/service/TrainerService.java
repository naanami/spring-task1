package com.epam.gymcrm.service;

import com.epam.gymcrm.dto.response.GeneratedCredentials;
import com.epam.gymcrm.entity.Trainer;
import com.epam.gymcrm.entity.Training;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.entity.User;
import com.epam.gymcrm.exception.NotFoundException;
import com.epam.gymcrm.repository.TraineeRepository;
import com.epam.gymcrm.repository.TrainerRepository;
import com.epam.gymcrm.repository.TrainingRepository;
import com.epam.gymcrm.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainerService {

    private static final Logger log = LoggerFactory.getLogger(TrainerService.class);

    private final TrainingRepository trainingRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;

    public TrainerService(UserService userService,
                          UserRepository userRepository,
                          TrainerRepository trainerRepository,
                          TrainingRepository trainingRepository, TraineeRepository traineeRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.trainerRepository = trainerRepository;
        this.trainingRepository = trainingRepository;
        this.traineeRepository = traineeRepository;
    }

    @Transactional
    public GeneratedCredentials createTrainerProfile(String firstName,
                                                     String lastName,
                                                     TrainingType specialization) {
        log.debug("Creating trainer profile for {} {}", firstName, lastName);

        if (traineeRepository.existsByUserFirstNameAndUserLastName(firstName, lastName)) {
            log.warn("Cannot create trainer profile because trainee profile already exists for {} {}", firstName, lastName);
            throw new IllegalArgumentException("User cannot be registered as both trainer and trainee");
        }

        GeneratedCredentials creds = userService.registerUser(firstName, lastName);

        User user = userRepository.findById(creds.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found: " + creds.getUserId()));

        Trainer trainer = new Trainer(user, specialization);
        trainerRepository.save(trainer);

        log.info("Trainer profile created: userId={}, specialization={}", creds.getUserId(), specialization);
        return creds;
    }

    @Transactional(readOnly = true)
    public Trainer selectTrainerProfile(String username) {
        log.debug("Selecting trainer profile: username={}", username);

        return trainerRepository.findDetailedByUserUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainer not found for username: " + username));
    }

    @Transactional(readOnly = true)
    public long countTrainers() {
        return trainerRepository.count();
    }

    @Transactional
    public void deleteAllTrainers() {
        var userIds = trainerRepository.findAll().stream()
                .map(t -> t.getUser().getId())
                .collect(Collectors.toSet());

        trainerRepository.deleteAll();
        userService.deleteUsers(userIds);
    }

    @Transactional(readOnly = true)
    public List<Training> getTrainerTrainings(String username,
                                              LocalDateTime from,
                                              LocalDateTime to,
                                              String traineeName) {
        return trainingRepository.findTrainerTrainings(username, from, to, traineeName);
    }

    @Transactional
    public Trainer updateTrainerProfile(String username,
                                        String firstName,
                                        String lastName,
                                        Boolean active) {
        log.info("Updating trainer profile: username={}", username);

        Trainer trainer = trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainer not found for username: " + username));

        User user = trainer.getUser();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setActive(active);

        userRepository.save(user);

        return trainerRepository.findDetailedByUserUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainer not found for username: " + username));
    }
}