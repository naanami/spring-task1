package com.epam.gymcrm.facade;

import com.epam.gymcrm.dto.response.TraineeProfileResponse;
import com.epam.gymcrm.dto.response.TraineeTrainingResponse;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeFacadeExtraTest {

    @Test
    void getTraineeProfileShouldMapEntityToResponse() {
        TraineeService service = mock(TraineeService.class);
        TraineeFacade facade = new TraineeFacade(service);

        User traineeUser = new User("Anna", "Smith", "anna.smith", "pass", true);
        Trainee trainee = new Trainee(traineeUser, LocalDate.of(2000, 1, 1), "Addr");

        User trainerUser = new User("John", "Doe", "john.doe", "pass", true);
        Trainer trainer = new Trainer(trainerUser, TrainingType.YOGA);
        trainee.addTrainer(trainer);

        when(service.selectTraineeProfile("anna.smith")).thenReturn(trainee);

        TraineeProfileResponse response = facade.getTraineeProfile("anna.smith");

        assertEquals("anna.smith", response.getUsername());
        assertEquals("Anna", response.getFirstName());
        assertEquals("Smith", response.getLastName());
        assertEquals("Addr", response.getAddress());
        assertTrue(response.isActive());
        assertEquals(1, response.getTrainers().size());
        assertEquals("john.doe", response.getTrainers().get(0).getUsername());
        assertEquals(TrainingType.YOGA, response.getTrainers().get(0).getSpecialization());
    }

    @Test
    void updateTraineeProfileShouldDelegateAndMap() {
        TraineeService service = mock(TraineeService.class);
        TraineeFacade facade = new TraineeFacade(service);

        User user = new User("Anna", "Smith", "anna.smith", "pass", true);
        Trainee trainee = new Trainee(user, LocalDate.of(2000, 1, 1), "New Addr");

        when(service.updateTraineeProfile("anna.smith", "Anna", "Smith",
                LocalDate.of(2000, 1, 1), "New Addr", true))
                .thenReturn(trainee);

        TraineeProfileResponse response = facade.updateTraineeProfile(
                "anna.smith", "Anna", "Smith", LocalDate.of(2000, 1, 1), "New Addr", true
        );

        assertEquals("anna.smith", response.getUsername());
        assertEquals("New Addr", response.getAddress());
    }

    @Test
    void getNotAssignedActiveTrainersShouldMapToSummaryResponses() {
        TraineeService service = mock(TraineeService.class);
        TraineeFacade facade = new TraineeFacade(service);

        Trainer trainer = new Trainer(new User("John", "Doe", "john.doe", "pass", true), TrainingType.YOGA);

        when(service.getNotAssignedTrainers("anna.smith")).thenReturn(List.of(trainer));

        List<TrainerSummaryResponse> result = facade.getNotAssignedActiveTrainers("anna.smith");

        assertEquals(1, result.size());
        assertEquals("john.doe", result.get(0).getUsername());
        assertEquals(TrainingType.YOGA, result.get(0).getSpecialization());
    }

    @Test
    void getTraineeTrainingResponsesShouldMapTrainings() {
        TraineeService service = mock(TraineeService.class);
        TraineeFacade facade = new TraineeFacade(service);

        Trainee trainee = new Trainee(new User("Anna", "Smith", "anna.smith", "pass", true), null, "Addr");
        Trainer trainer = new Trainer(new User("John", "Doe", "john.doe", "pass", true), TrainingType.YOGA);
        Training training = new Training(
                trainee, trainer, "Morning Yoga", TrainingType.YOGA,
                LocalDateTime.of(2026, 3, 20, 9, 0), 60
        );

        when(service.getTraineeTrainings("anna.smith", null, null, null, null))
                .thenReturn(List.of(training));

        List<TraineeTrainingResponse> result =
                facade.getTraineeTrainingResponses("anna.smith", null, null, null, null);

        assertEquals(1, result.size());
        assertEquals("Morning Yoga", result.get(0).getTrainingName());
        assertEquals("John Doe", result.get(0).getTrainerName());
    }
}