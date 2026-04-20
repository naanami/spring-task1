package com.epam.gymcrm.service;

import com.epam.gymcrm.dto.request.ActionType;
import com.epam.gymcrm.dto.request.TrainerWorkloadRequest;
import com.epam.gymcrm.entity.Trainer;
import com.epam.gymcrm.security.InternalJwtService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@Service
public class TrainerWorkloadIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(TrainerWorkloadIntegrationService.class);
    private static final String WORKLOAD_SERVICE_CB = "workloadService";
    private static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";

    private final RestTemplate restTemplate;
    private final String workloadServiceUrl;
    private final TransactionContextService transactionContextService;
    private final InternalJwtService internalJwtService;

    public TrainerWorkloadIntegrationService(RestTemplate restTemplate,
                                             @Value("${app.workload-service.url}") String workloadServiceUrl,
                                             TransactionContextService transactionContextService,
                                             InternalJwtService internalJwtService) {
        this.restTemplate = restTemplate;
        this.workloadServiceUrl = workloadServiceUrl;
        this.transactionContextService = transactionContextService;
        this.internalJwtService = internalJwtService;
    }

    @CircuitBreaker(name = WORKLOAD_SERVICE_CB, fallbackMethod = "fallbackSendTrainingAdded")
    public void sendTrainingAdded(Trainer trainer, LocalDate trainingDate, Integer duration) {
        TrainerWorkloadRequest request = buildRequest(trainer, trainingDate, duration, ActionType.ADD);
        HttpEntity<TrainerWorkloadRequest> entity = buildHttpEntity(request);

        String transactionId = transactionContextService.getOrCreateTransactionId();

        log.info("Sending ADD workload event: trainerUsername={}, date={}, duration={}, transactionId={}",
                trainer.getUser().getUsername(), trainingDate, duration, transactionId);

        ResponseEntity<Void> response = restTemplate.postForEntity(
                workloadServiceUrl + "/api/workloads",
                entity,
                Void.class
        );

        log.info("ADD workload event sent successfully: trainerUsername={}, status={}, transactionId={}",
                trainer.getUser().getUsername(), response.getStatusCode().value(), transactionId);
    }

    @CircuitBreaker(name = WORKLOAD_SERVICE_CB, fallbackMethod = "fallbackSendTrainingDeleted")
    public void sendTrainingDeleted(Trainer trainer, LocalDate trainingDate, Integer duration) {
        TrainerWorkloadRequest request = buildRequest(trainer, trainingDate, duration, ActionType.DELETE);
        HttpEntity<TrainerWorkloadRequest> entity = buildHttpEntity(request);

        String transactionId = transactionContextService.getOrCreateTransactionId();

        log.info("Sending DELETE workload event: trainerUsername={}, date={}, duration={}, transactionId={}",
                trainer.getUser().getUsername(), trainingDate, duration, transactionId);

        ResponseEntity<Void> response = restTemplate.postForEntity(
                workloadServiceUrl + "/api/workloads",
                entity,
                Void.class
        );

        log.info("DELETE workload event sent successfully: trainerUsername={}, status={}, transactionId={}",
                trainer.getUser().getUsername(), response.getStatusCode().value(), transactionId);
    }

    private HttpEntity<TrainerWorkloadRequest> buildHttpEntity(TrainerWorkloadRequest request) {
        String transactionId = transactionContextService.getOrCreateTransactionId();
        String token = internalJwtService.generateServiceToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set(TRANSACTION_ID_HEADER, transactionId);
        headers.setBearerAuth(token);

        return new HttpEntity<>(request, headers);
    }

    private TrainerWorkloadRequest buildRequest(Trainer trainer,
                                                LocalDate trainingDate,
                                                Integer duration,
                                                ActionType actionType) {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername(trainer.getUser().getUsername());
        request.setTrainerFirstName(trainer.getUser().getFirstName());
        request.setTrainerLastName(trainer.getUser().getLastName());
        request.setActive(trainer.getUser().isActive());
        request.setTrainingDate(trainingDate);
        request.setTrainingDuration(duration);
        request.setActionType(actionType);
        return request;
    }

    public void fallbackSendTrainingAdded(Trainer trainer,
                                          LocalDate trainingDate,
                                          Integer duration,
                                          Throwable throwable) {
        String transactionId = transactionContextService.getOrCreateTransactionId();

        log.error("Fallback triggered for ADD workload event: trainerUsername={}, date={}, duration={}, transactionId={}, error={}",
                trainer.getUser().getUsername(),
                trainingDate,
                duration,
                transactionId,
                throwable.getMessage());
    }

    public void fallbackSendTrainingDeleted(Trainer trainer,
                                            LocalDate trainingDate,
                                            Integer duration,
                                            Throwable throwable) {
        String transactionId = transactionContextService.getOrCreateTransactionId();

        log.error("Fallback triggered for DELETE workload event: trainerUsername={}, date={}, duration={}, transactionId={}, error={}",
                trainer.getUser().getUsername(),
                trainingDate,
                duration,
                transactionId,
                throwable.getMessage());
    }
}