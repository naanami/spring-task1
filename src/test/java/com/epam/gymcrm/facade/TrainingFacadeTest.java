package com.epam.gymcrm.facade;

import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.service.TrainingService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class TrainingFacadeTest {

    @Test
    void createTrainingShouldDelegateToService() {
        TrainingService service = mock(TrainingService.class);
        TrainingFacade facade = new TrainingFacade(service);

        UUID traineeId = UUID.randomUUID();
        UUID trainerId = UUID.randomUUID();
        LocalDateTime dt = LocalDateTime.of(2026, 1, 1, 10, 0);

        Training expected = new Training(UUID.randomUUID(), traineeId, trainerId, "Name",
                TrainingType.CARDIO, dt, 60);

        when(service.createTraining(traineeId, trainerId, "Name", TrainingType.CARDIO, dt, 60))
                .thenReturn(expected);

        Training actual = facade.createTraining(traineeId, trainerId, "Name", TrainingType.CARDIO, dt, 60);

        assertSame(expected, actual);
        verify(service).createTraining(traineeId, trainerId, "Name", TrainingType.CARDIO, dt, 60);
        verifyNoMoreInteractions(service);
    }
    @Test
    void selectTrainingShouldDelegateToService() {
        TrainingService service = mock(TrainingService.class);
        TrainingFacade facade = new TrainingFacade(service);

        UUID id = UUID.randomUUID();
        facade.selectTraining(id);

        verify(service).selectTraining(id);
        verifyNoMoreInteractions(service);
    }

    @Test
    void selectAllTrainingsShouldDelegateToService() {
        TrainingService service = mock(TrainingService.class);
        TrainingFacade facade = new TrainingFacade(service);

        facade.selectAllTrainings();

        verify(service).selectAllTrainings();
        verifyNoMoreInteractions(service);
    }

}
