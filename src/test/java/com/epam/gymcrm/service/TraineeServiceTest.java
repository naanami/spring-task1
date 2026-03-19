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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceTest {

    private TraineeRepository traineeRepository;
    private UserRepository userRepository;
    private UserService userService;
    private TrainerRepository trainerRepository;
    private TrainingRepository trainingRepository;
    private TraineeService traineeService;

    @BeforeEach
    void setUp() {
        traineeRepository = mock(TraineeRepository.class);
        userRepository = mock(UserRepository.class);
        userService = mock(UserService.class);
        trainerRepository = mock(TrainerRepository.class);
        trainingRepository = mock(TrainingRepository.class);

        traineeService = new TraineeService(
                traineeRepository,
                userRepository,
                userService,
                trainerRepository,
                trainingRepository
        );
    }

    @Test
    void createTraineeProfileShouldCreateTrainee() {
        UUID userId = UUID.randomUUID();
        GeneratedCredentials creds = new GeneratedCredentials(userId, "Anna.Smith", "secret");
        User user = new User("Anna", "Smith", "Anna.Smith", "secret", true);

        when(userService.registerUser("Anna", "Smith")).thenReturn(creds);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        GeneratedCredentials result = traineeService.createTraineeProfile(
                "Anna", "Smith", LocalDate.of(2000, 1, 1), "Yerevan"
        );

        assertEquals(creds, result);
        verify(traineeRepository).save(any(Trainee.class));
    }

    @Test
    void selectTraineeProfileShouldReturnTraineeByUsername() {
        Trainee trainee = mock(Trainee.class);
        when(traineeRepository.findByUserUsername("Anna.Smith")).thenReturn(Optional.of(trainee));

        Trainee result = traineeService.selectTraineeProfile("Anna.Smith");

        assertSame(trainee, result);
    }

    @Test
    void updateTraineeAddressShouldUpdateAddress() {
        Trainee trainee = new Trainee(mock(User.class), LocalDate.of(2000, 1, 1), "Old");
        when(traineeRepository.findByUserUsername("Anna.Smith")).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(any(Trainee.class))).thenAnswer(inv -> inv.getArgument(0));

        Trainee updated = traineeService.updateTraineeAddress("Anna.Smith", "New");

        assertEquals("New", updated.getAddress());
    }

    @Test
    void updateTraineeAddressShouldThrowIfMissing() {
        when(traineeRepository.findByUserUsername("Anna.Smith")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> traineeService.updateTraineeAddress("Anna.Smith", "New"));
    }

    @Test
    void deleteTraineeProfileShouldDeleteTraineeAndUser() {
        UUID userId = UUID.randomUUID();

        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);

        Trainer trainer = new Trainer(mock(User.class), TrainingType.YOGA);
        Trainee trainee = new Trainee(user, LocalDate.of(2000, 1, 1), "Addr");
        trainee.addTrainer(trainer);

        when(traineeRepository.findByUserUsername("Anna.Smith")).thenReturn(Optional.of(trainee));

        traineeService.deleteTraineeProfile("Anna.Smith");

        verify(traineeRepository).delete(trainee);
        verify(userService).deleteUser(userId);
        assertTrue(trainee.getTrainers().isEmpty());
        assertFalse(trainer.getTrainees().contains(trainee));
    }

    @Test
    void countTraineesShouldReturnRepositoryCount() {
        when(traineeRepository.count()).thenReturn(2L);

        assertEquals(2L, traineeService.countTrainees());
    }

    @Test
    void getTraineeTrainingsShouldReturnRepositoryResult() {
        List<Training> trainings = List.of(mock(Training.class));
        LocalDateTime from = LocalDateTime.now().minusDays(5);
        LocalDateTime to = LocalDateTime.now();

        when(trainingRepository.findTraineeTrainings("Anna.Smith", from, to, "John", TrainingType.YOGA))
                .thenReturn(trainings);

        List<Training> result = traineeService.getTraineeTrainings(
                "Anna.Smith", from, to, "John", TrainingType.YOGA
        );

        assertEquals(1, result.size());
    }

    @Test
    void getNotAssignedTrainersShouldReturnRepositoryResult() {
        List<Trainer> trainers = List.of(mock(Trainer.class), mock(Trainer.class));
        when(trainerRepository.findNotAssignedToTrainee("Anna.Smith")).thenReturn(trainers);

        List<Trainer> result = traineeService.getNotAssignedTrainers("Anna.Smith");

        assertEquals(2, result.size());
    }

    @Test
    void updateTraineeTrainersShouldAssignRequestedTrainers() {
        Trainee trainee = new Trainee(mock(User.class), LocalDate.of(2000, 1, 1), "Addr");
        Trainer trainer1 = new Trainer(mock(User.class), TrainingType.YOGA);
        Trainer trainer2 = new Trainer(mock(User.class), TrainingType.CARDIO);

        when(traineeRepository.findByUserUsername("Anna.Smith")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(trainer1));
        when(trainerRepository.findByUserUsername("Mike.Jones")).thenReturn(Optional.of(trainer2));

        traineeService.updateTraineeTrainers("Anna.Smith", List.of("John.Doe", "Mike.Jones"));

        assertEquals(2, trainee.getTrainers().size());
        verify(traineeRepository).save(trainee);
    }
}