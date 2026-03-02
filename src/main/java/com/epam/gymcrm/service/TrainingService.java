package com.epam.gymcrm.service;

import com.epam.gymcrm.dao.TraineeDao;
import com.epam.gymcrm.dao.TrainerDao;
import com.epam.gymcrm.dao.TrainingDao;
import com.epam.gymcrm.entity.Training;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TrainingService {

    private static final Logger log = LoggerFactory.getLogger(TrainingService.class);

    private TrainingDao trainingDao;
    private TraineeDao traineeDao;
    private TrainerDao trainerDao;

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    public Training createTraining(UUID traineeId,
                                   UUID trainerId,
                                   String trainingName,
                                   TrainingType trainingType,
                                   LocalDateTime trainingDate,
                                   int trainingDuration) {

        log.debug("Creating training: traineeId={}, trainerId={}, type={}, duration={}",
                traineeId, trainerId, trainingType, trainingDuration);

        validateTraineeExists(traineeId);
        validateTrainerExists(trainerId);
        validateDuration(trainingDuration);

        Training training = buildTraining(
                traineeId,
                trainerId,
                trainingName,
                trainingType,
                trainingDate,
                trainingDuration
        );

        Training saved = trainingDao.save(training);

        log.info("Training created: id={}, name={}", saved.getId(), trainingName);

        return saved;
    }

    private void validateTraineeExists(UUID traineeId) {
        if (traineeDao.findById(traineeId).isEmpty()) {
            log.warn("Trainee not found: {}", traineeId);
            throw new NotFoundException("Trainee not found: " + traineeId);
        }
    }

    private void validateTrainerExists(UUID trainerId) {
        if (trainerDao.findById(trainerId).isEmpty()) {
            log.warn("Trainer not found: {}", trainerId);
            throw new NotFoundException("Trainer not found: " + trainerId);
        }
    }

    private void validateDuration(int trainingDuration) {
        if (trainingDuration <= 0) {
            log.warn("Invalid training duration: {}", trainingDuration);
            throw new IllegalArgumentException("Training duration must be > 0");
        }
    }

    private Training buildTraining(UUID traineeId,
                                   UUID trainerId,
                                   String trainingName,
                                   TrainingType trainingType,
                                   LocalDateTime trainingDate,
                                   int trainingDuration) {

        return new Training(
                UUID.randomUUID(),
                traineeId,
                trainerId,
                trainingName,
                trainingType,
                trainingDate,
                trainingDuration
        );
    }

    public Optional<Training> selectTraining(UUID trainingId) {
        log.debug("Selecting training: id={}", trainingId);
        return trainingDao.findById(trainingId);
    }

    public List<Training> selectAllTrainings() {
        log.debug("Selecting all trainings");
        return trainingDao.findAll();
    }
    public long countTrainings() {
        return trainingDao.count();
    }

    public void deleteAllTrainings() {
        trainingDao.deleteAll();
    }

}
