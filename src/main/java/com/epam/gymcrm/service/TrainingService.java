package com.epam.gymcrm.service;

import com.epam.gymcrm.entity.Trainee;
import com.epam.gymcrm.entity.Trainer;
import com.epam.gymcrm.entity.Training;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.exception.NotFoundException;
import com.epam.gymcrm.repository.TraineeRepository;
import com.epam.gymcrm.repository.TrainerRepository;
import com.epam.gymcrm.repository.TrainingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TrainingService {

    private static final Logger log = LoggerFactory.getLogger(TrainingService.class);

    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    public TrainingService(TrainingRepository trainingRepository,
                           TraineeRepository traineeRepository,
                           TrainerRepository trainerRepository) {
        this.trainingRepository = trainingRepository;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
    }

    @Transactional
    public Training createTraining(UUID traineeUserId,
                                   UUID trainerUserId,
                                   String trainingName,
                                   TrainingType trainingType,
                                   LocalDateTime trainingDate,
                                   int trainingDuration) {

        log.debug("Creating training: traineeUserId={}, trainerUserId={}, type={}, duration={}",
                traineeUserId, trainerUserId, trainingType, trainingDuration);

        validateDuration(trainingDuration);

        Trainee trainee = traineeRepository.findByUserId(traineeUserId)
                .orElseThrow(() -> new NotFoundException("Trainee not found for userId: " + traineeUserId));

        Trainer trainer = trainerRepository.findByUserId(trainerUserId)
                .orElseThrow(() -> new NotFoundException("Trainer not found for userId: " + trainerUserId));

        Training training = new Training(
                trainee,
                trainer,
                trainingName,
                trainingType,
                trainingDate,
                trainingDuration
        );

        Training saved = trainingRepository.save(training);

        log.info("Training created: id={}, name={}", saved.getId(), trainingName);
        return saved;
    }

    private void validateDuration(int trainingDuration) {
        if (trainingDuration <= 0) {
            log.warn("Invalid training duration: {}", trainingDuration);
            throw new IllegalArgumentException("Training duration must be > 0");
        }
    }

    @Transactional(readOnly = true)
    public Optional<Training> selectTraining(UUID trainingId) {
        log.debug("Selecting training: id={}", trainingId);
        return trainingRepository.findById(trainingId);
    }

    @Transactional(readOnly = true)
    public List<Training> selectAllTrainings() {
        log.debug("Selecting all trainings");
        return trainingRepository.findAll();
    }

    @Transactional(readOnly = true)
    public long countTrainings() {
        return trainingRepository.count();
    }

    @Transactional
    public void deleteAllTrainings() {
        trainingRepository.deleteAll();
    }

    @Transactional
    public void createTraining(String traineeUsername,
                               String trainerUsername,
                               String trainingName,
                               TrainingType trainingType,
                               LocalDateTime trainingDate,
                               Integer duration) {

        validateDuration(duration);

        Trainee trainee = traineeRepository.findByUserUsername(traineeUsername)
                .orElseThrow(() -> new NotFoundException("Trainee not found: " + traineeUsername));

        Trainer trainer = trainerRepository.findByUserUsername(trainerUsername)
                .orElseThrow(() -> new NotFoundException("Trainer not found: " + trainerUsername));

        Training training = new Training(
                trainee,
                trainer,
                trainingName,
                trainingType,
                trainingDate,
                duration
        );

        trainingRepository.save(training);
    }
}