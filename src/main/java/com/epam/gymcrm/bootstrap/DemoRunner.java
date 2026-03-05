package com.epam.gymcrm.bootstrap;

import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.facade.TraineeFacade;
import com.epam.gymcrm.facade.TrainerFacade;
import com.epam.gymcrm.facade.TrainingFacade;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DemoRunner implements CommandLineRunner {

    private final TrainerFacade trainerFacade;
    private final TraineeFacade traineeFacade;
    private final TrainingFacade trainingFacade;

    public DemoRunner(TrainerFacade trainerFacade,
                      TraineeFacade traineeFacade,
                      TrainingFacade trainingFacade) {
        this.trainerFacade = trainerFacade;
        this.traineeFacade = traineeFacade;
        this.trainingFacade = trainingFacade;
    }

    @Override
    public void run(String... args) {

        System.out.println("===== GYM CRM DEMO =====");

        var trainer = trainerFacade.createTrainerProfile(
                "John",
                "Doe",
                TrainingType.YOGA
        );

        var trainee = traineeFacade.createTraineeProfile(
                "Anna",
                "Smith",
                LocalDate.of(2000, 5, 10),
                "Yerevan"
        );

        var training = trainingFacade.createTraining(
                trainer.getUsername(),
                trainer.getPassword(),
                trainee.getUserId(),
                trainer.getUserId(),
                "Morning Yoga",
                TrainingType.YOGA,
                LocalDateTime.now(),
                60
        );

        System.out.println("Trainer username: " + trainer.getUsername());
        System.out.println("Trainee username: " + trainee.getUsername());
        System.out.println("Training created: " + training.getId());

        System.out.println("===== DEMO END =====");
    }
}