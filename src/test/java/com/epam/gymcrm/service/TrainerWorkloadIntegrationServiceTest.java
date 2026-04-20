package com.epam.gymcrm.service;

import com.epam.gymcrm.dto.request.ActionType;
import com.epam.gymcrm.entity.Trainer;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.entity.User;
import com.epam.gymcrm.security.InternalJwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TrainerWorkloadIntegrationServiceTest {

    private RestTemplate restTemplate;
    private TransactionContextService transactionContextService;
    private InternalJwtService internalJwtService;
    private TrainerWorkloadIntegrationService service;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        transactionContextService = mock(TransactionContextService.class);
        internalJwtService = mock(InternalJwtService.class);

        service = new TrainerWorkloadIntegrationService(
                restTemplate,
                "http://trainer-workload-service",
                transactionContextService,
                internalJwtService
        );
    }

    @Test
    void sendTrainingAddedShouldCallWorkloadService() {
        Trainer trainer = buildTrainer();
        when(transactionContextService.getOrCreateTransactionId()).thenReturn("tx-123");
        when(internalJwtService.generateServiceToken()).thenReturn("token-123");
        when(restTemplate.postForEntity(anyString(), any(), eq(Void.class)))
                .thenReturn(ResponseEntity.ok().build());

        service.sendTrainingAdded(trainer, LocalDate.of(2026, 3, 24), 60);

        verify(restTemplate).postForEntity(
                eq("http://trainer-workload-service/api/workloads"),
                any(),
                eq(Void.class)
        );
    }

    @Test
    void sendTrainingDeletedShouldCallWorkloadService() {
        Trainer trainer = buildTrainer();
        when(transactionContextService.getOrCreateTransactionId()).thenReturn("tx-123");
        when(internalJwtService.generateServiceToken()).thenReturn("token-123");
        when(restTemplate.postForEntity(anyString(), any(), eq(Void.class)))
                .thenReturn(ResponseEntity.ok().build());

        service.sendTrainingDeleted(trainer, LocalDate.of(2026, 3, 24), 60);

        verify(restTemplate).postForEntity(
                eq("http://trainer-workload-service/api/workloads"),
                any(),
                eq(Void.class)
        );
    }

    @Test
    void fallbackSendTrainingAddedShouldNotThrow() {
        Trainer trainer = buildTrainer();
        when(transactionContextService.getOrCreateTransactionId()).thenReturn("tx-123");

        assertDoesNotThrow(() ->
                service.fallbackSendTrainingAdded(trainer, LocalDate.of(2026, 3, 24), 60, new RuntimeException("fail"))
        );
    }

    @Test
    void fallbackSendTrainingDeletedShouldNotThrow() {
        Trainer trainer = buildTrainer();
        when(transactionContextService.getOrCreateTransactionId()).thenReturn("tx-123");

        assertDoesNotThrow(() ->
                service.fallbackSendTrainingDeleted(trainer, LocalDate.of(2026, 3, 24), 60, new RuntimeException("fail"))
        );
    }

    private Trainer buildTrainer() {
        User user = new User("John", "Doe", "John.Doe", "pass", true);
        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());
        return new Trainer(user, TrainingType.YOGA);
    }
}