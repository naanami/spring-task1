package com.epam.gymcrm.service;

import com.epam.gymcrm.dto.message.TrainerWorkloadMessage;
import com.epam.gymcrm.entity.Trainer;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jms.core.JmsTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TrainerWorkloadIntegrationServiceTest {

    private JmsTemplate jmsTemplate;
    private TransactionContextService transactionContextService;
    private TrainerWorkloadIntegrationService service;

    @BeforeEach
    void setUp() {
        jmsTemplate = mock(JmsTemplate.class);
        transactionContextService = mock(TransactionContextService.class);

        service = new TrainerWorkloadIntegrationService(
                jmsTemplate,
                transactionContextService,
                "trainer.workload.queue"
        );
    }

    @Test
    void sendTrainingAddedShouldPublishAddMessage() {
        Trainer trainer = buildTrainer();
        LocalDate date = LocalDate.of(2026, 5, 10);

        when(transactionContextService.getOrCreateTransactionId()).thenReturn("tx-123");

        service.sendTrainingAdded(trainer, date, 60);

        ArgumentCaptor<TrainerWorkloadMessage> captor =
                ArgumentCaptor.forClass(TrainerWorkloadMessage.class);

        verify(jmsTemplate).convertAndSend(eq("trainer.workload.queue"), captor.capture());

        TrainerWorkloadMessage message = captor.getValue();

        assertEquals("John.Doe", message.getTrainerUsername());
        assertEquals("John", message.getTrainerFirstName());
        assertEquals("Doe", message.getTrainerLastName());
        assertEquals(true, message.getActive());
        assertEquals(date, message.getTrainingDate());
        assertEquals(60, message.getTrainingDuration());
        assertEquals("ADD", message.getActionType().name());
        assertEquals("tx-123", message.getTransactionId());
    }
    @Test
    void sendTrainingAddedShouldUseQueueNameFromConstructor() {
        Trainer trainer = buildTrainer();
        LocalDate date = LocalDate.of(2026, 5, 10);

        when(transactionContextService.getOrCreateTransactionId()).thenReturn("tx-789");

        service.sendTrainingAdded(trainer, date, 45);

        verify(jmsTemplate).convertAndSend(eq("trainer.workload.queue"), any(TrainerWorkloadMessage.class));
    }

    @Test
    void sendTrainingAddedShouldPropagateJmsException() {
        Trainer trainer = buildTrainer();
        LocalDate date = LocalDate.of(2026, 5, 10);

        when(transactionContextService.getOrCreateTransactionId()).thenReturn("tx-error");
        doThrow(new org.springframework.jms.JmsException("broker unavailable") {})
                .when(jmsTemplate)
                .convertAndSend(eq("trainer.workload.queue"), any(TrainerWorkloadMessage.class));

        org.junit.jupiter.api.Assertions.assertThrows(
                org.springframework.jms.JmsException.class,
                () -> service.sendTrainingAdded(trainer, date, 60)
        );
    }
    @Test
    void sendTrainingDeletedShouldPropagateJmsException() {
        Trainer trainer = buildTrainer();
        LocalDate date = LocalDate.of(2026, 5, 10);

        when(transactionContextService.getOrCreateTransactionId()).thenReturn("tx-error");
        doThrow(new org.springframework.jms.JmsException("broker unavailable") {})
                .when(jmsTemplate)
                .convertAndSend(eq("trainer.workload.queue"), any(TrainerWorkloadMessage.class));

        org.junit.jupiter.api.Assertions.assertThrows(
                org.springframework.jms.JmsException.class,
                () -> service.sendTrainingDeleted(trainer, date, 30)
        );
    }
    @Test
    void sendTrainingDeletedShouldPublishDeleteMessage() {
        Trainer trainer = buildTrainer();
        LocalDate date = LocalDate.of(2026, 5, 10);

        when(transactionContextService.getOrCreateTransactionId()).thenReturn("tx-456");

        service.sendTrainingDeleted(trainer, date, 30);

        ArgumentCaptor<TrainerWorkloadMessage> captor =
                ArgumentCaptor.forClass(TrainerWorkloadMessage.class);

        verify(jmsTemplate).convertAndSend(eq("trainer.workload.queue"), captor.capture());

        TrainerWorkloadMessage message = captor.getValue();

        assertEquals("John.Doe", message.getTrainerUsername());
        assertEquals(date, message.getTrainingDate());
        assertEquals(30, message.getTrainingDuration());
        assertEquals("DELETE", message.getActionType().name());
        assertEquals("tx-456", message.getTransactionId());
    }

    private Trainer buildTrainer() {
        User user = new User("John", "Doe", "John.Doe", "password", true);
        return new Trainer(user, TrainingType.YOGA);
    }
}