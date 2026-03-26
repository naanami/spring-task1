package com.epam.gymcrm.service;

import com.epam.gymcrm.entity.Trainee;
import com.epam.gymcrm.entity.Trainer;
import com.epam.gymcrm.entity.Training;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.entity.User;
import com.epam.gymcrm.exception.NotFoundException;
import com.epam.gymcrm.repository.TraineeRepository;
import com.epam.gymcrm.repository.TrainerRepository;
import com.epam.gymcrm.repository.TrainingRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceTest {

    private TrainingRepository trainingRepository;
    private TraineeRepository traineeRepository;
    private TrainerRepository trainerRepository;
    private TrainingService trainingService;
    private MeterRegistry meterRegistry;



    @BeforeEach
    void setUp() {
        meterRegistry = mock(MeterRegistry.class);
        trainingRepository = mock(TrainingRepository.class);
        traineeRepository = mock(TraineeRepository.class);
        trainerRepository = mock(TrainerRepository.class);
        Counter counter = mock(Counter.class);
        when(meterRegistry.counter("trainings.created")).thenReturn(counter);

        trainingService = new TrainingService(
                trainingRepository,
                traineeRepository,
                trainerRepository,
                meterRegistry
        );
    }

    @Test
    void createTrainingShouldCreateWhenTraineeAndTrainerExist() {
        UUID traineeUserId = UUID.randomUUID();
        UUID trainerUserId = UUID.randomUUID();

        Trainee trainee = new Trainee(mock(User.class), LocalDate.of(2000, 1, 1), "Addr");
        Trainer trainer = new Trainer(mock(User.class), TrainingType.CARDIO);

        when(traineeRepository.findByUserId(traineeUserId)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserId(trainerUserId)).thenReturn(Optional.of(trainer));
        when(trainingRepository.save(any(Training.class))).thenAnswer(inv -> inv.getArgument(0));

        Training training = trainingService.createTraining(
                traineeUserId,
                trainerUserId,
                "Morning",
                TrainingType.CARDIO,
                LocalDateTime.of(2026, 1, 1, 10, 0),
                60
        );

        assertEquals("Morning", training.getTrainingName());
        assertEquals(60, training.getTrainingDuration());
        assertEquals(TrainingType.CARDIO, training.getTrainingType());
    }

    @Test
    void createTrainingShouldThrowIfDurationInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
                trainingService.createTraining(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "X",
                        TrainingType.CARDIO,
                        LocalDateTime.now(),
                        0
                ));
    }

    @Test
    void createTrainingShouldThrowIfTraineeMissing() {
        UUID traineeUserId = UUID.randomUUID();
        UUID trainerUserId = UUID.randomUUID();

        when(traineeRepository.findByUserId(traineeUserId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                trainingService.createTraining(
                        traineeUserId,
                        trainerUserId,
                        "X",
                        TrainingType.CARDIO,
                        LocalDateTime.now(),
                        30
                ));
    }

    @Test
    void createTrainingShouldThrowIfTrainerMissing() {
        UUID traineeUserId = UUID.randomUUID();
        UUID trainerUserId = UUID.randomUUID();

        Trainee trainee = new Trainee(mock(User.class), LocalDate.of(2000, 1, 1), "Addr");
        when(traineeRepository.findByUserId(traineeUserId)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserId(trainerUserId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                trainingService.createTraining(
                        traineeUserId,
                        trainerUserId,
                        "X",
                        TrainingType.CARDIO,
                        LocalDateTime.now(),
                        30
                ));
    }
    @Test
    void countTrainingsShouldReturnRepositoryCount() {
        when(trainingRepository.count()).thenReturn(4L);

        long result = trainingService.countTrainings();

        assertEquals(4L, result);
    }

    @Test
    void selectTrainingShouldDelegateToRepository() {
        UUID id = UUID.randomUUID();
        Training training = mock(Training.class);
        when(trainingRepository.findById(id)).thenReturn(Optional.of(training));

        Optional<Training> result = trainingService.selectTraining(id);

        assertTrue(result.isPresent());
        assertSame(training, result.get());
    }

    @Test
    void deleteAllTrainingsShouldDelegateToRepository() {
        trainingService.deleteAllTrainings();
        verify(trainingRepository).deleteAll();
    }
}