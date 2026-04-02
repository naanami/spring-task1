package com.epam.gymcrm.facade;

import com.epam.gymcrm.dto.response.TrainerProfileResponse;
import com.epam.gymcrm.dto.response.TrainerTrainingResponse;
import com.epam.gymcrm.entity.Trainee;
import com.epam.gymcrm.entity.Trainer;
import com.epam.gymcrm.entity.Training;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.entity.User;
import com.epam.gymcrm.service.TrainerService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerFacadeExtraTest {

    @Test
    void getTrainerProfileShouldMapEntityToResponse() {
        TrainerService service = mock(TrainerService.class);
        TrainerFacade facade = new TrainerFacade(service);

        User trainerUser = new User("John", "Doe", "john.doe", "pass", true);
        Trainer trainer = new Trainer(trainerUser, TrainingType.YOGA);

        Trainee trainee = new Trainee(new User("Anna", "Smith", "anna.smith", "pass", true),
                LocalDate.of(2000, 1, 1), "Addr");
        trainer.getTrainees().add(trainee);

        when(service.selectTrainerProfile("john.doe")).thenReturn(trainer);

        TrainerProfileResponse response = facade.getTrainerProfile("john.doe");

        assertEquals("john.doe", response.getUsername());
        assertEquals("John", response.getFirstName());
        assertEquals(TrainingType.YOGA, response.getSpecialization());
        assertEquals(1, response.getTrainees().size());
        assertEquals("anna.smith", response.getTrainees().get(0).getUsername());
    }

    @Test
    void updateTrainerProfileShouldDelegateAndMap() {
        TrainerService service = mock(TrainerService.class);
        TrainerFacade facade = new TrainerFacade(service);

        User trainerUser = new User("Mike", "Jones", "john.doe", "pass", false);
        Trainer trainer = new Trainer(trainerUser, TrainingType.YOGA);

        when(service.updateTrainerProfile("john.doe", "Mike", "Jones", false)).thenReturn(trainer);

        TrainerProfileResponse response = facade.updateTrainerProfile("john.doe", "Mike", "Jones", false);

        assertEquals("Mike", response.getFirstName());
        assertFalse(response.isActive());
        assertEquals(TrainingType.YOGA, response.getSpecialization());
    }

    @Test
    void getTrainerTrainingResponsesShouldMapTrainings() {
        TrainerService service = mock(TrainerService.class);
        TrainerFacade facade = new TrainerFacade(service);

        Trainee trainee = new Trainee(new User("Anna", "Smith", "anna.smith", "pass", true),
                LocalDate.of(2000, 1, 1), "Addr");
        Trainer trainer = new Trainer(new User("John", "Doe", "john.doe", "pass", true), TrainingType.YOGA);

        Training training = new Training(
                trainee, trainer, "Morning Yoga", TrainingType.YOGA,
                LocalDateTime.of(2026, 3, 20, 9, 0), 60
        );

        when(service.getTrainerTrainings("john.doe", null, null, null)).thenReturn(List.of(training));

        List<TrainerTrainingResponse> result =
                facade.getTrainerTrainingResponses("john.doe", null, null, null);

        assertEquals(1, result.size());
        assertEquals("Morning Yoga", result.get(0).getTrainingName());
        assertEquals("Anna Smith", result.get(0).getTraineeName());
    }
}