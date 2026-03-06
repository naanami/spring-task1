package com.epam.gymcrm.service;

import com.epam.gymcrm.dto.GeneratedCredentials;
import com.epam.gymcrm.entity.Trainer;
import com.epam.gymcrm.entity.Training;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.entity.User;
import com.epam.gymcrm.exception.NotFoundException;
import com.epam.gymcrm.repository.TrainerRepository;
import com.epam.gymcrm.repository.TrainingRepository;
import com.epam.gymcrm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceTest {

    private UserService userService;
    private UserRepository userRepository;
    private TrainerRepository trainerRepository;
    private TrainingRepository trainingRepository;
    private TrainerService trainerService;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        userRepository = mock(UserRepository.class);
        trainerRepository = mock(TrainerRepository.class);
        trainingRepository = mock(TrainingRepository.class);

        trainerService = new TrainerService(
                userService,
                userRepository,
                trainerRepository,
                trainingRepository
        );
    }

    @Test
    void createTrainerProfileShouldCreateTrainer() {
        UUID userId = UUID.randomUUID();
        GeneratedCredentials creds = new GeneratedCredentials(userId, "John.Doe", "secret");
        User user = new User("John", "Doe", "John.Doe", "secret", true);

        when(userService.registerUser("John", "Doe")).thenReturn(creds);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        GeneratedCredentials result =
                trainerService.createTrainerProfile("John", "Doe", TrainingType.YOGA);

        assertEquals(creds, result);
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    void updateTrainerProfileShouldUpdateSpecialization() {
        User user = new User("John", "Doe", "John.Doe", "secret", true);
        Trainer trainer = new Trainer(user, TrainingType.CARDIO);

        when(trainerRepository.findByUserId(any())).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(inv -> inv.getArgument(0));

        Trainer updated = trainerService.updateTrainerProfile(UUID.randomUUID(), TrainingType.YOGA);

        assertEquals(TrainingType.YOGA, updated.getSpecialization());
    }

    @Test
    void updateTrainerProfileShouldThrowIfTrainerMissing() {
        when(trainerRepository.findByUserId(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> trainerService.updateTrainerProfile(UUID.randomUUID(), TrainingType.YOGA));
    }

    @Test
    void getTrainerTrainingsShouldReturnRepositoryResult() {
        List<Training> trainings = List.of(mock(Training.class));
        LocalDateTime from = LocalDateTime.now().minusDays(10);
        LocalDateTime to = LocalDateTime.now();

        when(trainingRepository.findTrainerTrainings("John.Doe", from, to, "Anna"))
                .thenReturn(trainings);

        List<Training> result =
                trainerService.getTrainerTrainings("John.Doe", from, to, "Anna");

        assertEquals(1, result.size());
    }
    @Test
    void countTrainersShouldReturnRepositoryCount() {
        when(trainerRepository.count()).thenReturn(3L);
        assertEquals(3L, trainerService.countTrainers());
    }

    @Test
    void selectTrainerProfileShouldDelegateToRepository() {
        UUID id = UUID.randomUUID();
        Trainer trainer = mock(Trainer.class);
        when(trainerRepository.findByUserId(id)).thenReturn(Optional.of(trainer));

        Optional<Trainer> result = trainerService.selectTrainerProfile(id);

        assertTrue(result.isPresent());
        assertSame(trainer, result.get());
    }
}