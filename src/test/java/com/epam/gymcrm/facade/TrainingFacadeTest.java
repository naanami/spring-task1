package com.epam.gymcrm.facade;

import com.epam.gymcrm.entity.Training;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.security.SecurityAccessService;
import com.epam.gymcrm.service.TrainingService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingFacadeTest {

    @Test
    void createTrainingShouldCheckAccessThenDelegate() {
        TrainingService service = mock(TrainingService.class);
        SecurityAccessService accessService = mock(SecurityAccessService.class);
        TrainingFacade facade = new TrainingFacade(service, accessService);

        LocalDateTime dt = LocalDateTime.of(2026, 1, 1, 10, 0);

        facade.createTraining(
                "user", "anna.smith", "john.doe", "Name", TrainingType.CARDIO, dt, 60
        );

        verify(accessService).ensureSameUser("user");
        verify(service).createTraining("anna.smith", "john.doe", "Name", TrainingType.CARDIO, dt, 60);
    }

    @Test
    void selectTrainingShouldDelegate() {
        TrainingService service = mock(TrainingService.class);
        SecurityAccessService accessService = mock(SecurityAccessService.class);
        TrainingFacade facade = new TrainingFacade(service, accessService);

        UUID id = UUID.randomUUID();
        Optional<Training> expected = Optional.of(mock(Training.class));
        when(service.selectTraining(id)).thenReturn(expected);

        Optional<Training> actual = facade.selectTraining(id);

        assertSame(expected, actual);
        verify(service).selectTraining(id);
    }

    @Test
    void countTrainingsShouldDelegate() {
        TrainingService service = mock(TrainingService.class);
        SecurityAccessService accessService = mock(SecurityAccessService.class);
        TrainingFacade facade = new TrainingFacade(service, accessService);

        when(service.countTrainings()).thenReturn(5L);

        long result = facade.countTrainings();

        assertEquals(5L, result);
        verify(service).countTrainings();
    }

    @Test
    void selectAllTrainingsShouldDelegate() {
        TrainingService service = mock(TrainingService.class);
        SecurityAccessService accessService = mock(SecurityAccessService.class);
        TrainingFacade facade = new TrainingFacade(service, accessService);

        List<Training> expected = List.of(mock(Training.class));
        when(service.selectAllTrainings()).thenReturn(expected);

        List<Training> actual = facade.selectAllTrainings();

        assertSame(expected, actual);
        verify(service).selectAllTrainings();
    }

    @Test
    void deleteAllTrainingsShouldDelegate() {
        TrainingService service = mock(TrainingService.class);
        SecurityAccessService accessService = mock(SecurityAccessService.class);
        TrainingFacade facade = new TrainingFacade(service, accessService);

        facade.deleteAllTrainings();

        verify(service).deleteAllTrainings();
    }
}