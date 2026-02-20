package com.epam.gymcrm.facade;

import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.dto.GeneratedCredentials;
import com.epam.gymcrm.service.TrainerService;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class TrainerFacadeTest {

    @Test
    void createTrainerProfileShouldDelegateToService() {
        TrainerService service = mock(TrainerService.class);
        TrainerFacade facade = new TrainerFacade(service);

        GeneratedCredentials expected =
                new GeneratedCredentials(UUID.randomUUID(), "A.B", "secret");
        when(service.createTrainerProfile("A", "B", TrainingType.CARDIO)).thenReturn(expected);

        GeneratedCredentials actual = facade.createTrainerProfile("A", "B", TrainingType.CARDIO);

        assertSame(expected, actual);
        verify(service).createTrainerProfile("A", "B", TrainingType.CARDIO);
        verifyNoMoreInteractions(service);
    }
    @Test
    void selectTrainerProfileShouldDelegateToService() {
        TrainerService service = mock(TrainerService.class);
        TrainerFacade facade = new TrainerFacade(service);

        UUID id = UUID.randomUUID();
        facade.selectTrainerProfile(id);

        verify(service).selectTrainerProfile(id);
        verifyNoMoreInteractions(service);
    }

    @Test
    void updateTrainerProfileShouldDelegateToService() {
        TrainerService service = mock(TrainerService.class);
        TrainerFacade facade = new TrainerFacade(service);

        UUID id = UUID.randomUUID();
        facade.updateTrainerProfile(id, TrainingType.YOGA);

        verify(service).updateTrainerProfile(id, TrainingType.YOGA);
        verifyNoMoreInteractions(service);
    }

}
