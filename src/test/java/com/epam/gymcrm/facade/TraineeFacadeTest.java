package com.epam.gymcrm.facade;

import com.epam.gymcrm.dto.response.GeneratedCredentials;
import com.epam.gymcrm.dto.response.TrainerSummaryResponse;
import com.epam.gymcrm.entity.Trainee;
import com.epam.gymcrm.entity.Trainer;
import com.epam.gymcrm.entity.Training;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.entity.User;
import com.epam.gymcrm.service.TraineeService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeFacadeTest {

    @Test
    void createTraineeProfileShouldDelegate() {
        TraineeService service = mock(TraineeService.class);
        TraineeFacade facade = new TraineeFacade(service);

        LocalDate dob = LocalDate.of(2000, 1, 1);
        GeneratedCredentials expected =
                new GeneratedCredentials(UUID.randomUUID(), "A.B", "secret");
        when(service.createTraineeProfile("A", "B", dob, "Addr")).thenReturn(expected);

        GeneratedCredentials actual = facade.createTraineeProfile("A", "B", dob, "Addr");

        assertSame(expected, actual);
        verify(service).createTraineeProfile("A", "B", dob, "Addr");
    }

    @Test
    void selectTraineeProfileShouldDelegate() {
        TraineeService service = mock(TraineeService.class);
        TraineeFacade facade = new TraineeFacade(service);

        Trainee trainee = mock(Trainee.class);
        when(service.selectTraineeProfile("user")).thenReturn(trainee);

        Trainee result = facade.selectTraineeProfile("user");

        assertSame(trainee, result);
        verify(service).selectTraineeProfile("user");
    }

    @Test
    void updateTraineeAddressShouldDelegate() {
        TraineeService service = mock(TraineeService.class);
        TraineeFacade facade = new TraineeFacade(service);

        Trainee trainee = mock(Trainee.class);
        when(service.updateTraineeAddress("user", "New")).thenReturn(trainee);

        Trainee result = facade.updateTraineeAddress("user", "New");

        assertSame(trainee, result);
        verify(service).updateTraineeAddress("user", "New");
    }

    @Test
    void deleteTraineeProfileShouldDelegate() {
        TraineeService service = mock(TraineeService.class);
        TraineeFacade facade = new TraineeFacade(service);

        facade.deleteTraineeProfile("user");

        verify(service).deleteTraineeProfile("user");
    }

    @Test
    void countTraineesShouldDelegate() {
        TraineeService service = mock(TraineeService.class);
        TraineeFacade facade = new TraineeFacade(service);

        when(service.countTrainees()).thenReturn(2L);

        long result = facade.countTrainees();

        assertEquals(2L, result);
        verify(service).countTrainees();
    }

    @Test
    void deleteAllTraineesShouldDelegate() {
        TraineeService service = mock(TraineeService.class);
        TraineeFacade facade = new TraineeFacade(service);

        facade.deleteAllTrainees();

        verify(service).deleteAllTrainees();
    }

    @Test
    void getTraineeTrainingsShouldDelegate() {
        TraineeService service = mock(TraineeService.class);
        TraineeFacade facade = new TraineeFacade(service);

        List<Training> trainings = List.of(mock(Training.class));
        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now();

        when(service.getTraineeTrainings("user", from, to, "John", TrainingType.YOGA))
                .thenReturn(trainings);

        List<Training> result = facade.getTraineeTrainings(
                "user", from, to, "John", TrainingType.YOGA
        );

        assertSame(trainings, result);
        verify(service).getTraineeTrainings("user", from, to, "John", TrainingType.YOGA);
    }

    @Test
    void getNotAssignedTrainersShouldDelegate() {
        TraineeService service = mock(TraineeService.class);
        TraineeFacade facade = new TraineeFacade(service);

        List<Trainer> trainers = List.of(mock(Trainer.class));
        when(service.getNotAssignedTrainers("user")).thenReturn(trainers);

        List<Trainer> result = facade.getNotAssignedTrainers("user");

        assertSame(trainers, result);
        verify(service).getNotAssignedTrainers("user");
    }

    @Test
    void updateTraineeTrainersShouldDelegateAndReturnSummaries() {
        TraineeService service = mock(TraineeService.class);
        TraineeFacade facade = new TraineeFacade(service);

        User traineeUser = new User("Anna", "Smith", "anna.smith", "pass", true);
        Trainee trainee = new Trainee(traineeUser, LocalDate.of(2000, 1, 1), "Addr");

        User trainerUser = new User("John", "Doe", "john.doe", "pass", true);
        Trainer trainer = new Trainer(trainerUser, TrainingType.YOGA);
        trainee.addTrainer(trainer);

        when(service.selectTraineeProfile("user")).thenReturn(trainee);

        List<TrainerSummaryResponse> result =
                facade.updateTraineeTrainers("user", List.of("john.doe"));

        assertEquals(1, result.size());
        assertEquals("john.doe", result.get(0).getUsername());

        verify(service).updateTraineeTrainers("user", List.of("john.doe"));
        verify(service).selectTraineeProfile("user");
    }
}