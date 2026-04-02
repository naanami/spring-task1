package com.epam.gymcrm.facade;

import com.epam.gymcrm.dto.response.GeneratedCredentials;
import com.epam.gymcrm.entity.Trainer;
import com.epam.gymcrm.entity.Training;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.service.TrainerService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerFacadeTest {

    @Test
    void createTrainerProfileShouldDelegate() {
        TrainerService service = mock(TrainerService.class);
        TrainerFacade facade = new TrainerFacade(service);

        GeneratedCredentials expected =
                new GeneratedCredentials(UUID.randomUUID(), "A.B", "secret");
        when(service.createTrainerProfile("A", "B", TrainingType.CARDIO)).thenReturn(expected);

        GeneratedCredentials actual = facade.createTrainerProfile("A", "B", TrainingType.CARDIO);

        assertSame(expected, actual);
        verify(service).createTrainerProfile("A", "B", TrainingType.CARDIO);
    }

    @Test
    void getTrainerTrainingsShouldDelegate() {
        TrainerService service = mock(TrainerService.class);
        TrainerFacade facade = new TrainerFacade(service);

        List<Training> trainings = List.of(mock(Training.class));
        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now();

        when(service.getTrainerTrainings("user", from, to, "Anna")).thenReturn(trainings);

        List<Training> result = facade.getTrainerTrainings("user", from, to, "Anna");

        assertSame(trainings, result);
        verify(service).getTrainerTrainings("user", from, to, "Anna");
    }
}