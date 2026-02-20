package com.epam.gymcrm.service;

import com.epam.gymcrm.dao.TraineeDao;
import com.epam.gymcrm.dao.TrainerDao;
import com.epam.gymcrm.dao.TrainingDao;
import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.domain.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class TrainingServiceTest {

    private TrainingService trainingService;
    private TraineeDao traineeDao;
    private TrainerDao trainerDao;

    @BeforeEach
    void setup() {
        Map<UUID, Trainee> traineeStorage = new ConcurrentHashMap<>();
        Map<UUID, Trainer> trainerStorage = new ConcurrentHashMap<>();
        Map<UUID, Training> trainingStorage = new ConcurrentHashMap<>();

        traineeDao = new TraineeDao();
        traineeDao.setStorage(traineeStorage);

        trainerDao = new TrainerDao();
        trainerDao.setStorage(trainerStorage);

        TrainingDao trainingDao = new TrainingDao();
        trainingDao.setStorage(trainingStorage);

        trainingService = new TrainingService();
        trainingService.setTraineeDao(traineeDao);
        trainingService.setTrainerDao(trainerDao);
        trainingService.setTrainingDao(trainingDao);
    }

    @Test
    void createTrainingShouldCreateWhenTraineeAndTrainerExist() {
        UUID traineeId = UUID.randomUUID();
        UUID trainerId = UUID.randomUUID();

        traineeDao.save(new Trainee(traineeId, LocalDate.of(1999, 1, 1), "Addr"));
        trainerDao.save(new Trainer(trainerId, TrainingType.CARDIO));

        Training training = trainingService.createTraining(
                traineeId,
                trainerId,
                "Morning",
                TrainingType.CARDIO,
                LocalDateTime.of(2026, 1, 1, 10, 0),
                60
        );

        assertNotNull(training.getId());
        assertEquals(traineeId, training.getTraineeId());
        assertEquals(trainerId, training.getTrainerId());
        assertEquals(60, training.getTrainingDuration());
    }

    @Test
    void createTrainingShouldThrowIfDurationInvalid() {
        UUID traineeId = UUID.randomUUID();
        UUID trainerId = UUID.randomUUID();

        traineeDao.save(new Trainee(traineeId, LocalDate.of(1999, 1, 1), "Addr"));
        trainerDao.save(new Trainer(trainerId, TrainingType.CARDIO));

        assertThrows(IllegalArgumentException.class, () ->
                trainingService.createTraining(
                        traineeId, trainerId, "X", TrainingType.CARDIO, LocalDateTime.now(), 0
                )
        );
    }

    @Test
    void createTrainingShouldThrowIfTraineeMissing() {
        UUID traineeId = UUID.randomUUID();
        UUID trainerId = UUID.randomUUID();
        trainerDao.save(new Trainer(trainerId, TrainingType.CARDIO));

        assertThrows(RuntimeException.class, () ->
                trainingService.createTraining(
                        traineeId, trainerId, "X", TrainingType.CARDIO, LocalDateTime.now(), 30
                )
        );
    }

    @Test
    void createTrainingShouldThrowIfTrainerMissing() {
        UUID traineeId = UUID.randomUUID();
        UUID trainerId = UUID.randomUUID();
        traineeDao.save(new Trainee(traineeId, LocalDate.of(1999, 1, 1), "Addr"));

        assertThrows(RuntimeException.class, () ->
                trainingService.createTraining(
                        traineeId, trainerId, "X", TrainingType.CARDIO, LocalDateTime.now(), 30
                )
        );
    }
}
