package com.epam.gymcrm.facade;

import com.epam.gymcrm.dto.GeneratedCredentials;
import com.epam.gymcrm.entity.Trainer;
import com.epam.gymcrm.entity.Training;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.entity.User;
import com.epam.gymcrm.service.AuthService;
import com.epam.gymcrm.service.TrainerService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class TrainerFacadeTest {

    @Test
    void createTrainerProfileShouldDelegateWithoutAuth() {
        TrainerService service = mock(TrainerService.class);
        AuthService authService = mock(AuthService.class);
        TrainerFacade facade = new TrainerFacade(service, authService);

        GeneratedCredentials expected =
                new GeneratedCredentials(UUID.randomUUID(), "A.B", "secret");
        when(service.createTrainerProfile("A", "B", TrainingType.CARDIO)).thenReturn(expected);

        GeneratedCredentials actual = facade.createTrainerProfile("A", "B", TrainingType.CARDIO);

        assertSame(expected, actual);
        verify(service).createTrainerProfile("A", "B", TrainingType.CARDIO);
        verifyNoInteractions(authService);
    }

    @Test
    void selectTrainerProfileShouldAuthenticateThenDelegate() {
        TrainerService service = mock(TrainerService.class);
        AuthService authService = mock(AuthService.class);
        TrainerFacade facade = new TrainerFacade(service, authService);

        Trainer expected = mock(Trainer.class);
        when(authService.authenticate("user", "pass")).thenReturn(mock(User.class));
        when(service.selectTrainerProfile("user")).thenReturn(expected);

        Trainer actual = facade.selectTrainerProfile("user", "pass");

        assertSame(expected, actual);
        verify(authService).authenticate("user", "pass");
        verify(service).selectTrainerProfile("user");
    }

    @Test
    void updateTrainerProfileShouldAuthenticateThenDelegate() {
        TrainerService service = mock(TrainerService.class);
        AuthService authService = mock(AuthService.class);
        TrainerFacade facade = new TrainerFacade(service, authService);

        Trainer trainer = mock(Trainer.class);
        when(authService.authenticate("user", "pass")).thenReturn(mock(User.class));
        when(service.updateTrainerProfile("user", TrainingType.YOGA)).thenReturn(trainer);

        Trainer actual = facade.updateTrainerProfile("user", "pass", TrainingType.YOGA);

        assertSame(trainer, actual);
        verify(authService).authenticate("user", "pass");
        verify(service).updateTrainerProfile("user", TrainingType.YOGA);
    }

    @Test
    void countTrainersShouldAuthenticateThenDelegate() {
        TrainerService service = mock(TrainerService.class);
        AuthService authService = mock(AuthService.class);
        TrainerFacade facade = new TrainerFacade(service, authService);

        when(authService.authenticate("user", "pass")).thenReturn(mock(User.class));
        when(service.countTrainers()).thenReturn(3L);

        long result = facade.countTrainers("user", "pass");

        assertEquals(3L, result);
        verify(authService).authenticate("user", "pass");
        verify(service).countTrainers();
    }

    @Test
    void deleteAllTrainersShouldAuthenticateThenDelegate() {
        TrainerService service = mock(TrainerService.class);
        AuthService authService = mock(AuthService.class);
        TrainerFacade facade = new TrainerFacade(service, authService);

        when(authService.authenticate("user", "pass")).thenReturn(mock(User.class));

        facade.deleteAllTrainers("user", "pass");

        verify(authService).authenticate("user", "pass");
        verify(service).deleteAllTrainers();
    }

    @Test
    void getTrainerTrainingsShouldAuthenticateThenDelegate() {
        TrainerService service = mock(TrainerService.class);
        AuthService authService = mock(AuthService.class);
        TrainerFacade facade = new TrainerFacade(service, authService);

        List<Training> trainings = List.of(mock(Training.class));
        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now();

        when(authService.authenticate("user", "pass")).thenReturn(mock(User.class));
        when(service.getTrainerTrainings("user", from, to, "Anna")).thenReturn(trainings);

        List<Training> result = facade.getTrainerTrainings("user", "pass", from, to, "Anna");

        assertSame(trainings, result);
        verify(authService).authenticate("user", "pass");
        verify(service).getTrainerTrainings("user", from, to, "Anna");
    }
}