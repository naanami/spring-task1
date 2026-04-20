package com.epam.gymcrm.service;

import com.epam.gymcrm.entity.Trainee;
import com.epam.gymcrm.entity.Trainer;
import com.epam.gymcrm.entity.Training;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.entity.User;
import com.epam.gymcrm.repository.TraineeRepository;
import com.epam.gymcrm.repository.TrainerRepository;
import com.epam.gymcrm.repository.TrainingRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TrainingServiceTest {

    private TrainingRepository trainingRepository;
    private TraineeRepository traineeRepository;
    private TrainerRepository trainerRepository;
    private TrainerWorkloadIntegrationService trainerWorkloadIntegrationService;
    private TrainingService trainingService;

    @BeforeEach
    void setUp() {
        trainingRepository = mock(TrainingRepository.class);
        traineeRepository = mock(TraineeRepository.class);
        trainerRepository = mock(TrainerRepository.class);
        trainerWorkloadIntegrationService = mock(TrainerWorkloadIntegrationService.class);

        trainingService = new TrainingService(
                trainingRepository,
                traineeRepository,
                trainerRepository,
                trainerWorkloadIntegrationService,
                new SimpleMeterRegistry()
        );
    }

    @Test
    void createTrainingShouldNotifyWorkloadService() {
        UUID traineeUserId = UUID.randomUUID();
        UUID trainerUserId = UUID.randomUUID();
        LocalDateTime trainingDate = LocalDateTime.of(2026, 3, 24, 10, 0);

        User traineeUser = new User("Lana", "Banana", "Lana.Banana", "pass", true);
        ReflectionTestUtils.setField(traineeUser, "id", traineeUserId);

        User trainerUser = new User("John", "Doe", "John.Doe", "pass", true);
        ReflectionTestUtils.setField(trainerUser, "id", trainerUserId);

        Trainee trainee = new Trainee(traineeUser, LocalDate.of(2000, 1, 1), "Address");
        Trainer trainer = new Trainer(trainerUser, TrainingType.YOGA);

        when(traineeRepository.findByUserId(traineeUserId)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserId(trainerUserId)).thenReturn(Optional.of(trainer));
        when(trainingRepository.save(any(Training.class))).thenAnswer(invocation -> invocation.getArgument(0));

        trainingService.createTraining(
                traineeUserId,
                trainerUserId,
                "Morning Yoga",
                TrainingType.YOGA,
                trainingDate,
                60
        );

        verify(trainerWorkloadIntegrationService).sendTrainingAdded(
                eq(trainer),
                eq(trainingDate.toLocalDate()),
                eq(60)
        );
    }
}