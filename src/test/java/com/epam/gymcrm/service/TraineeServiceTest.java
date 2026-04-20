package com.epam.gymcrm.service;

import com.epam.gymcrm.entity.Trainee;
import com.epam.gymcrm.entity.Trainer;
import com.epam.gymcrm.entity.Training;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.entity.User;
import com.epam.gymcrm.repository.TraineeRepository;
import com.epam.gymcrm.repository.TrainerRepository;
import com.epam.gymcrm.repository.TrainingRepository;
import com.epam.gymcrm.repository.UserRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

class TraineeServiceTest {

    private TraineeRepository traineeRepository;
    private UserRepository userRepository;
    private UserService userService;
    private TrainerRepository trainerRepository;
    private TrainingRepository trainingRepository;
    private TrainerWorkloadIntegrationService trainerWorkloadIntegrationService;
    private TraineeService traineeService;

    @BeforeEach
    void setUp() {
        traineeRepository = mock(TraineeRepository.class);
        userRepository = mock(UserRepository.class);
        userService = mock(UserService.class);
        trainerRepository = mock(TrainerRepository.class);
        trainingRepository = mock(TrainingRepository.class);
        trainerWorkloadIntegrationService = mock(TrainerWorkloadIntegrationService.class);

        traineeService = new TraineeService(
                traineeRepository,
                userRepository,
                userService,
                trainerRepository,
                trainingRepository,
                trainerWorkloadIntegrationService,
                new SimpleMeterRegistry()
        );
    }

    @Test
    void deleteTraineeProfileShouldNotifyWorkloadServiceForDeletedTrainings() {
        UUID traineeUserId = UUID.randomUUID();

        User traineeUser = new User("Lana", "Banana", "Lana.Banana", "pass", true);
        ReflectionTestUtils.setField(traineeUser, "id", traineeUserId);

        User trainerUser = new User("John", "Doe", "John.Doe", "pass", true);

        Trainer trainer = new Trainer(trainerUser, TrainingType.YOGA);
        Trainee trainee = new Trainee(traineeUser, LocalDate.of(2000, 1, 1), "Address");

        Training training = new Training(
                trainee,
                trainer,
                "Morning Yoga",
                TrainingType.YOGA,
                LocalDateTime.of(2026, 3, 24, 10, 0),
                60
        );

        when(traineeRepository.findByUserUsername("Lana.Banana")).thenReturn(Optional.of(trainee));
        when(trainingRepository.findByTrainee_User_Username("Lana.Banana")).thenReturn(List.of(training));

        traineeService.deleteTraineeProfile("Lana.Banana");

        verify(trainerWorkloadIntegrationService).sendTrainingDeleted(
                eq(trainer),
                eq(training.getTrainingDate().toLocalDate()),
                eq(60)
        );

        verify(traineeRepository).delete(trainee);
        verify(userService).deleteUser(traineeUserId);
    }
}