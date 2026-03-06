package com.epam.gymcrm.facade;

import com.epam.gymcrm.dto.GeneratedCredentials;
import com.epam.gymcrm.entity.Trainer;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.entity.User;
import com.epam.gymcrm.service.AuthService;
import com.epam.gymcrm.service.TrainerService;
import org.junit.jupiter.api.Test;

import java.util.Optional;
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

        UUID userId = UUID.randomUUID();
        Optional<Trainer> expected = Optional.of(mock(Trainer.class));
        when(authService.authenticate("user", "pass")).thenReturn(mock(User.class));
        when(service.selectTrainerProfile(userId)).thenReturn(expected);

        Optional<Trainer> actual = facade.selectTrainerProfile("user", "pass", userId);

        assertSame(expected, actual);
        verify(authService).authenticate("user", "pass");
        verify(service).selectTrainerProfile(userId);
    }

    @Test
    void updateTrainerProfileShouldAuthenticateThenDelegate() {
        TrainerService service = mock(TrainerService.class);
        AuthService authService = mock(AuthService.class);
        TrainerFacade facade = new TrainerFacade(service, authService);

        UUID userId = UUID.randomUUID();
        Trainer trainer = mock(Trainer.class);
        when(authService.authenticate("user", "pass")).thenReturn(mock(User.class));
        when(service.updateTrainerProfile(userId, TrainingType.YOGA)).thenReturn(trainer);

        Trainer actual = facade.updateTrainerProfile("user", "pass", userId, TrainingType.YOGA);

        assertSame(trainer, actual);
        verify(authService).authenticate("user", "pass");
        verify(service).updateTrainerProfile(userId, TrainingType.YOGA);
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
}