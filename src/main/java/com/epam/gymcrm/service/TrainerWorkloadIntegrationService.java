package com.epam.gymcrm.service;

import com.epam.gymcrm.dto.message.TrainerWorkloadMessage;
import com.epam.gymcrm.dto.request.ActionType;
import com.epam.gymcrm.entity.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TrainerWorkloadIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(TrainerWorkloadIntegrationService.class);

    private final JmsTemplate jmsTemplate;
    private final TransactionContextService transactionContextService;
    private final String workloadQueue;

    public TrainerWorkloadIntegrationService(JmsTemplate jmsTemplate,
                                             TransactionContextService transactionContextService,
                                             @Value("${app.jms.trainer-workload-queue}") String workloadQueue) {
        this.jmsTemplate = jmsTemplate;
        this.transactionContextService = transactionContextService;
        this.workloadQueue = workloadQueue;
    }

    public void sendTrainingAdded(Trainer trainer, LocalDate trainingDate, Integer duration) {
        TrainerWorkloadMessage message = buildMessage(trainer, trainingDate, duration, ActionType.ADD);
        sendMessage(message);
    }

    public void sendTrainingDeleted(Trainer trainer, LocalDate trainingDate, Integer duration) {
        TrainerWorkloadMessage message = buildMessage(trainer, trainingDate, duration, ActionType.DELETE);
        sendMessage(message);
    }

    private void sendMessage(TrainerWorkloadMessage message) {
        log.info("Sending trainer workload JMS message: trainerUsername={}, actionType={}, date={}, duration={}, transactionId={}",
                message.getTrainerUsername(),
                message.getActionType(),
                message.getTrainingDate(),
                message.getTrainingDuration(),
                message.getTransactionId());

        jmsTemplate.convertAndSend(workloadQueue, message);

        log.info("Trainer workload JMS message sent successfully: queue={}, trainerUsername={}, actionType={}, transactionId={}",
                workloadQueue,
                message.getTrainerUsername(),
                message.getActionType(),
                message.getTransactionId());
    }

    private TrainerWorkloadMessage buildMessage(Trainer trainer,
                                                LocalDate trainingDate,
                                                Integer duration,
                                                ActionType actionType) {
        TrainerWorkloadMessage message = new TrainerWorkloadMessage();
        message.setTrainerUsername(trainer.getUser().getUsername());
        message.setTrainerFirstName(trainer.getUser().getFirstName());
        message.setTrainerLastName(trainer.getUser().getLastName());
        message.setActive(trainer.getUser().isActive());
        message.setTrainingDate(trainingDate);
        message.setTrainingDuration(duration);
        message.setActionType(actionType);
        message.setTransactionId(transactionContextService.getOrCreateTransactionId());
        return message;
    }
}