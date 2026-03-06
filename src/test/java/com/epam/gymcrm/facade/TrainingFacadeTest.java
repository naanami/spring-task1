package com.epam.gymcrm.facade;

import com.epam.gymcrm.entity.Training;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.entity.User;
import com.epam.gymcrm.service.AuthService;
import com.epam.gymcrm.service.TrainingService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class TrainingFacadeTest {

    @Test
    void createTrainingShouldAuthenticateThenDelegate() {
        TrainingService service = mock(TrainingService.class);
        AuthService authService = mock(AuthService.class);
        TrainingFacade facade = new TrainingFacade(service, authService);

        UUID traineeId = UUID.randomUUID();
        UUID trainerId = UUID.randomUUID();
        LocalDateTime dt = LocalDateTime.of(2026, 1, 1, 10, 0);

        Training expected = mock(Training.class);

        when(authService.authenticate("user", "pass")).thenReturn(mock(User.class));
        when(service.createTraining(traineeId, trainerId, "Name", TrainingType.CARDIO, dt, 60))
                .thenReturn(expected);

        Training actual = facade.createTraining(
                "user", "pass", traineeId, trainerId, "Name", TrainingType.CARDIO, dt, 60
        );

        assertSame(expected, actual);
        verify(authService).authenticate("user", "pass");
        verify(service).createTraining(traineeId, trainerId, "Name", TrainingType.CARDIO, dt, 60);
    }

    @Test
    void selectTrainingShouldAuthenticateThenDelegate() {
        TrainingService service = mock(TrainingService.class);
        AuthService authService = mock(AuthService.class);
        TrainingFacade facade = new TrainingFacade(service, authService);

        UUID id = UUID.randomUUID();
        Optional<Training> expected = Optional.of(mock(Training.class));
        when(authService.authenticate("user", "pass")).thenReturn(mock(User.class));
        when(service.selectTraining(id)).thenReturn(expected);

        Optional<Training> actual = facade.selectTraining("user", "pass", id);

        assertSame(expected, actual);
        verify(authService).authenticate("user", "pass");
        verify(service).selectTraining(id);
    }

    @Test
    void countTrainingsShouldAuthenticateThenDelegate() {
        TrainingService service = mock(TrainingService.class);
        AuthService authService = mock(AuthService.class);
        TrainingFacade facade = new TrainingFacade(service, authService);

        when(authService.authenticate("user", "pass")).thenReturn(mock(User.class));
        when(service.countTrainings()).thenReturn(5L);

        long result = facade.countTrainings("user", "pass");

        assertEquals(5L, result);
        verify(authService).authenticate("user", "pass");
        verify(service).countTrainings();
    }

    @Test
    void selectAllTrainingsShouldAuthenticateThenDelegate() {
        TrainingService service = mock(TrainingService.class);
        AuthService authService = mock(AuthService.class);
        TrainingFacade facade = new TrainingFacade(service, authService);

        List<Training> expected = List.of(mock(Training.class));
        when(authService.authenticate("user", "pass")).thenReturn(mock(User.class));
        when(service.selectAllTrainings()).thenReturn(expected);

        List<Training> actual = facade.selectAllTrainings("user", "pass");

        assertSame(expected, actual);
        verify(authService).authenticate("user", "pass");
        verify(service).selectAllTrainings();
    }
    @Test
    void deleteAllTrainingsShouldAuthenticateThenDelegate() {
        TrainingService service = mock(TrainingService.class);
        AuthService authService = mock(AuthService.class);
        TrainingFacade facade = new TrainingFacade(service, authService);

        when(authService.authenticate("user", "pass")).thenReturn(mock(User.class));

        facade.deleteAllTrainings("user", "pass");

        verify(authService).authenticate("user", "pass");
        verify(service).deleteAllTrainings();
    }
}