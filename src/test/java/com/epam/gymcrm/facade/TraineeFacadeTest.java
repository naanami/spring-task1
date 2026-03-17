package com.epam.gymcrm.facade;

import com.epam.gymcrm.dto.GeneratedCredentials;
import com.epam.gymcrm.entity.Trainee;
import com.epam.gymcrm.entity.Trainer;
import com.epam.gymcrm.entity.Training;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.entity.User;
import com.epam.gymcrm.service.AuthService;
import com.epam.gymcrm.service.TraineeService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class TraineeFacadeTest {

    @Test
    void createTraineeProfileShouldDelegateWithoutAuth() {
        TraineeService service = mock(TraineeService.class);
        AuthService authService = mock(AuthService.class);
        TraineeFacade facade = new TraineeFacade(service, authService);

        LocalDate dob = LocalDate.of(2000, 1, 1);
        GeneratedCredentials expected =
                new GeneratedCredentials(UUID.randomUUID(), "A.B", "secret");
        when(service.createTraineeProfile("A", "B", dob, "Addr")).thenReturn(expected);

        GeneratedCredentials actual = facade.createTraineeProfile("A", "B", dob, "Addr");

        assertSame(expected, actual);
        verify(service).createTraineeProfile("A", "B", dob, "Addr");
        verifyNoInteractions(authService);
    }

    @Test
    void selectTraineeProfileShouldAuthenticateThenDelegate() {
        TraineeService service = mock(TraineeService.class);
        AuthService authService = mock(AuthService.class);
        TraineeFacade facade = new TraineeFacade(service, authService);

        Trainee trainee = mock(Trainee.class);
        when(authService.authenticate("user", "pass")).thenReturn(mock(User.class));
        when(service.selectTraineeProfile("user")).thenReturn(trainee);

        Trainee result = facade.selectTraineeProfile("user", "pass");

        assertSame(trainee, result);
        verify(authService).authenticate("user", "pass");
        verify(service).selectTraineeProfile("user");
    }

    @Test
    void updateTraineeAddressShouldAuthenticateThenDelegate() {
        TraineeService service = mock(TraineeService.class);
        AuthService authService = mock(AuthService.class);
        TraineeFacade facade = new TraineeFacade(service, authService);

        Trainee trainee = mock(Trainee.class);
        when(authService.authenticate("user", "pass")).thenReturn(mock(User.class));
        when(service.updateTraineeAddress("user", "New")).thenReturn(trainee);

        Trainee result = facade.updateTraineeAddress("user", "pass", "New");

        assertSame(trainee, result);
        verify(authService).authenticate("user", "pass");
        verify(service).updateTraineeAddress("user", "New");
    }

    @Test
    void deleteTraineeProfileShouldAuthenticateThenDelegate() {
        TraineeService service = mock(TraineeService.class);
        AuthService authService = mock(AuthService.class);
        TraineeFacade facade = new TraineeFacade(service, authService);

        when(authService.authenticate("user", "pass")).thenReturn(mock(User.class));

        facade.deleteTraineeProfile("user", "pass");

        verify(authService).authenticate("user", "pass");
        verify(service).deleteTraineeProfile("user");
    }

    @Test
    void countTraineesShouldAuthenticateThenDelegate() {
        TraineeService service = mock(TraineeService.class);
        AuthService authService = mock(AuthService.class);
        TraineeFacade facade = new TraineeFacade(service, authService);

        when(authService.authenticate("user", "pass")).thenReturn(mock(User.class));
        when(service.countTrainees()).thenReturn(2L);

        long result = facade.countTrainees("user", "pass");

        assertEquals(2L, result);
        verify(authService).authenticate("user", "pass");
        verify(service).countTrainees();
    }

    @Test
    void deleteAllTraineesShouldAuthenticateThenDelegate() {
        TraineeService service = mock(TraineeService.class);
        AuthService authService = mock(AuthService.class);
        TraineeFacade facade = new TraineeFacade(service, authService);

        when(authService.authenticate("user", "pass")).thenReturn(mock(User.class));

        facade.deleteAllTrainees("user", "pass");

        verify(authService).authenticate("user", "pass");
        verify(service).deleteAllTrainees();
    }

    @Test
    void getTraineeTrainingsShouldAuthenticateThenDelegate() {
        TraineeService service = mock(TraineeService.class);
        AuthService authService = mock(AuthService.class);
        TraineeFacade facade = new TraineeFacade(service, authService);

        List<Training> trainings = List.of(mock(Training.class));
        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now();

        when(authService.authenticate("user", "pass")).thenReturn(mock(User.class));
        when(service.getTraineeTrainings("user", from, to, "John", TrainingType.YOGA))
                .thenReturn(trainings);

        List<Training> result = facade.getTraineeTrainings(
                "user", "pass", from, to, "John", TrainingType.YOGA
        );

        assertSame(trainings, result);
        verify(authService).authenticate("user", "pass");
        verify(service).getTraineeTrainings("user", from, to, "John", TrainingType.YOGA);
    }

    @Test
    void getNotAssignedTrainersShouldAuthenticateThenDelegate() {
        TraineeService service = mock(TraineeService.class);
        AuthService authService = mock(AuthService.class);
        TraineeFacade facade = new TraineeFacade(service, authService);

        List<Trainer> trainers = List.of(mock(Trainer.class));
        when(authService.authenticate("user", "pass")).thenReturn(mock(User.class));
        when(service.getNotAssignedTrainers("user")).thenReturn(trainers);

        List<Trainer> result = facade.getNotAssignedTrainers("user", "pass");

        assertSame(trainers, result);
        verify(authService).authenticate("user", "pass");
        verify(service).getNotAssignedTrainers("user");
    }

    @Test
    void updateTraineeTrainersShouldAuthenticateThenDelegate() {
        TraineeService service = mock(TraineeService.class);
        AuthService authService = mock(AuthService.class);
        TraineeFacade facade = new TraineeFacade(service, authService);

        List<String> trainerUsernames = List.of("John.Doe", "Mike.Jones");
        when(authService.authenticate("user", "pass")).thenReturn(mock(User.class));

        facade.updateTraineeTrainers("user", "pass", trainerUsernames);

        verify(authService).authenticate("user", "pass");
        verify(service).updateTraineeTrainers("user", trainerUsernames);
    }
}