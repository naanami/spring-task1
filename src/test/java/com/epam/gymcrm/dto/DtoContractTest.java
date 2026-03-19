package com.epam.gymcrm.dto;

import com.epam.gymcrm.dto.request.*;
import com.epam.gymcrm.dto.response.ErrorResponse;
import com.epam.gymcrm.dto.response.GeneratedCredentials;
import com.epam.gymcrm.dto.response.TraineeSummaryResponse;
import com.epam.gymcrm.dto.response.TraineeTrainingResponse;
import com.epam.gymcrm.dto.response.TrainerSummaryResponse;
import com.epam.gymcrm.dto.response.TrainerTrainingResponse;
import com.epam.gymcrm.entity.TrainingType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DtoContractTest {

    @Nested
    class RequestDtos {

        @Test
        void shouldRepresentTraineeRegistrationInput() {
            TraineeRegistrationRequest request = new TraineeRegistrationRequest();
            LocalDate dateOfBirth = LocalDate.of(2000, 1, 1);

            request.setFirstName("Anna");
            request.setLastName("Smith");
            request.setDateOfBirth(dateOfBirth);
            request.setAddress("Yerevan");

            assertEquals("Anna", request.getFirstName());
            assertEquals("Smith", request.getLastName());
            assertEquals(dateOfBirth, request.getDateOfBirth());
            assertEquals("Yerevan", request.getAddress());
        }

        @Test
        void shouldRepresentTrainerRegistrationInput() {
            TrainerRegistrationRequest request = new TrainerRegistrationRequest();

            request.setFirstName("John");
            request.setLastName("Doe");
            request.setSpecialization(TrainingType.YOGA);

            assertEquals("John", request.getFirstName());
            assertEquals("Doe", request.getLastName());
            assertEquals(TrainingType.YOGA, request.getSpecialization());
        }

        @Test
        void shouldRepresentLoginInput() {
            LoginRequest request = new LoginRequest();

            request.setUsername("john.doe");
            request.setPassword("secret");

            assertEquals("john.doe", request.getUsername());
            assertEquals("secret", request.getPassword());
        }

        @Test
        void shouldRepresentPasswordChangeInput() {
            ChangePasswordRequest request = new ChangePasswordRequest();

            request.setUsername("john.doe");
            request.setOldPassword("oldPass");
            request.setNewPassword("newPass");

            assertEquals("john.doe", request.getUsername());
            assertEquals("oldPass", request.getOldPassword());
            assertEquals("newPass", request.getNewPassword());
        }

        @Test
        void shouldRepresentTraineeProfileUpdateInput() {
            UpdateTraineeRequest request = new UpdateTraineeRequest();
            LocalDate dateOfBirth = LocalDate.of(2001, 5, 10);

            request.setFirstName("Anna");
            request.setLastName("Smith");
            request.setDateOfBirth(dateOfBirth);
            request.setAddress("New address");
            request.setActive(true);

            assertEquals("Anna", request.getFirstName());
            assertEquals("Smith", request.getLastName());
            assertEquals(dateOfBirth, request.getDateOfBirth());
            assertEquals("New address", request.getAddress());
            assertTrue(request.getActive());
        }

        @Test
        void shouldRepresentTrainerProfileUpdateInput() {
            UpdateTrainerRequest request = new UpdateTrainerRequest();

            request.setFirstName("Mike");
            request.setLastName("Jones");
            request.setActive(false);

            assertEquals("Mike", request.getFirstName());
            assertEquals("Jones", request.getLastName());
            assertFalse(request.getActive());
        }

        @Test
        void shouldRepresentTrainerAssignmentInput() {
            UpdateTraineeTrainersRequest request = new UpdateTraineeTrainersRequest();
            List<String> trainerUsernames = List.of("john.doe", "mike.jones");

            request.setTrainerUsernames(trainerUsernames);

            assertEquals(trainerUsernames, request.getTrainerUsernames());
            assertEquals(2, request.getTrainerUsernames().size());
        }

        @Test
        void shouldRepresentTrainingCreationInput() {
            CreateTrainingRequest request = new CreateTrainingRequest();
            LocalDateTime trainingDate = LocalDateTime.of(2026, 3, 20, 9, 0);

            request.setTraineeUsername("anna.smith");
            request.setTrainerUsername("john.doe");
            request.setTrainingName("Morning Yoga");
            request.setTrainingType(TrainingType.YOGA);
            request.setTrainingDate(trainingDate);
            request.setTrainingDuration(60);

            assertEquals("anna.smith", request.getTraineeUsername());
            assertEquals("john.doe", request.getTrainerUsername());
            assertEquals("Morning Yoga", request.getTrainingName());
            assertEquals(TrainingType.YOGA, request.getTrainingType());
            assertEquals(trainingDate, request.getTrainingDate());
            assertEquals(60, request.getTrainingDuration());
        }
    }

    @Nested
    class ResponseDtos {

        @Test
        void shouldRepresentGeneratedCredentialsResponse() {
            UUID userId = UUID.randomUUID();

            GeneratedCredentials response =
                    new GeneratedCredentials(userId, "john.doe", "secret123");

            assertEquals(userId, response.getUserId());
            assertEquals("john.doe", response.getUsername());
            assertEquals("secret123", response.getPassword());
        }

        @Test
        void shouldRepresentErrorResponse() {
            ErrorResponse response = new ErrorResponse(400, "Bad request");

            assertEquals(400, response.getStatus());
            assertEquals("Bad request", response.getMessage());
        }

        @Test
        void shouldRepresentTrainerSummaryResponse() {
            TrainerSummaryResponse response =
                    new TrainerSummaryResponse("john.doe", "John", "Doe", TrainingType.YOGA);

            assertEquals("john.doe", response.getUsername());
            assertEquals("John", response.getFirstName());
            assertEquals("Doe", response.getLastName());
            assertEquals(TrainingType.YOGA, response.getSpecialization());
        }

        @Test
        void shouldRepresentActivationInput() {
            ActivationRequest request = new ActivationRequest();

            request.setIsActive(true);

            assertTrue(request.getIsActive());
        }

        @Test
        void shouldRepresentTraineeSummaryResponse() {
            TraineeSummaryResponse response =
                    new TraineeSummaryResponse("anna.smith", "Anna", "Smith");

            assertEquals("anna.smith", response.getUsername());
            assertEquals("Anna", response.getFirstName());
            assertEquals("Smith", response.getLastName());
        }

        @Test
        void shouldRepresentTraineeTrainingView() {
            LocalDateTime trainingDate = LocalDateTime.of(2026, 3, 20, 9, 0);

            TraineeTrainingResponse response =
                    new TraineeTrainingResponse(
                            "Morning Yoga",
                            trainingDate,
                            TrainingType.YOGA,
                            60,
                            "John Doe"
                    );

            assertEquals("Morning Yoga", response.getTrainingName());
            assertEquals(trainingDate, response.getTrainingDate());
            assertEquals(TrainingType.YOGA, response.getTrainingType());
            assertEquals(60, response.getTrainingDuration());
            assertEquals("John Doe", response.getTrainerName());
        }

        @Test
        void shouldRepresentTrainerTrainingView() {
            LocalDateTime trainingDate = LocalDateTime.of(2026, 3, 20, 9, 0);

            TrainerTrainingResponse response =
                    new TrainerTrainingResponse(
                            "Morning Yoga",
                            trainingDate,
                            TrainingType.YOGA,
                            60,
                            "Anna Smith"
                    );

            assertEquals("Morning Yoga", response.getTrainingName());
            assertEquals(trainingDate, response.getTrainingDate());
            assertEquals(TrainingType.YOGA, response.getTrainingType());
            assertEquals(60, response.getTrainingDuration());
            assertEquals("Anna Smith", response.getTraineeName());
        }
    }
}