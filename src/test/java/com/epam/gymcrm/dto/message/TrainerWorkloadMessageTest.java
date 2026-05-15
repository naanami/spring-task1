package com.epam.gymcrm.dto.message;

import com.epam.gymcrm.dto.request.ActionType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TrainerWorkloadMessageTest {

    @Test
    void gettersAndSettersShouldWork() {
        TrainerWorkloadMessage message = new TrainerWorkloadMessage();

        LocalDate date = LocalDate.of(2026, 5, 10);

        message.setTrainerUsername("John.Doe");
        message.setTrainerFirstName("John");
        message.setTrainerLastName("Doe");
        message.setActive(true);
        message.setTrainingDate(date);
        message.setTrainingDuration(60);
        message.setActionType(ActionType.ADD);
        message.setTransactionId("tx-123");

        assertEquals("John.Doe", message.getTrainerUsername());
        assertEquals("John", message.getTrainerFirstName());
        assertEquals("Doe", message.getTrainerLastName());
        assertTrue(message.getActive());
        assertEquals(date, message.getTrainingDate());
        assertEquals(60, message.getTrainingDuration());
        assertEquals(ActionType.ADD, message.getActionType());
        assertEquals("tx-123", message.getTransactionId());
    }
}