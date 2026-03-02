package com.epam.gymcrm;

import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.facade.TraineeFacade;
import com.epam.gymcrm.facade.TrainerFacade;
import com.epam.gymcrm.facade.TrainingFacade;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Application {

    public static void main(String[] args) {

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext("com.epam.gymcrm")) {

            TrainerFacade trainerFacade = context.getBean(TrainerFacade.class);
            TraineeFacade traineeFacade = context.getBean(TraineeFacade.class);
            TrainingFacade trainingFacade = context.getBean(TrainingFacade.class);

            System.out.println("=== GYM CRM DEMO (Trainers & Trainees only) ===\n");

            printCounts(trainerFacade, traineeFacade, trainingFacade, "Initial counts");

            System.out.println("\n--- Reset domain data ---");
            trainingFacade.deleteAllTrainings();
            traineeFacade.deleteAllTrainees();
            trainerFacade.deleteAllTrainers();

            printCounts(trainerFacade, traineeFacade, trainingFacade, "After reset");

            System.out.println("\n--- Username uniqueness via Trainers ---");
            var tr1 = trainerFacade.createTrainerProfile("Joon", "Lee", TrainingType.YOGA);
            var tr2 = trainerFacade.createTrainerProfile("Joon", "Lee", TrainingType.PILATES);
            var tr3 = trainerFacade.createTrainerProfile("Joon", "Lee", TrainingType.CARDIO);

            System.out.println("Trainer #1 -> id=" + tr1.getUserId()
                    + ", username=" + tr1.getUsername()
                    + ", specialization=" + TrainingType.YOGA);

            System.out.println("Trainer #2 -> id=" + tr2.getUserId()
                    + ", username=" + tr2.getUsername()
                    + ", specialization=" + TrainingType.PILATES);

            System.out.println("Trainer #3 -> id=" + tr3.getUserId()
                    + ", username=" + tr3.getUsername()
                    + ", specialization=" + TrainingType.CARDIO);

            System.out.println("Trainers count = " + trainerFacade.countTrainers());

            System.out.println("\n--- Create Trainees ---");
            var ta1 = traineeFacade.createTraineeProfile(
                    "Anna",
                    "Smith",
                    LocalDate.of(2000, 5, 10),
                    "Yerevan"
            );
            var ta2 = traineeFacade.createTraineeProfile(
                    "Anna",
                    "Smith",
                    LocalDate.of(2000, 5, 10),
                    "Yerevan"
            );

            System.out.println("Trainee #1 -> id=" + ta1.getUserId()
                    + ", username=" + ta1.getUsername()
                    + ", address=Yerevan");

            System.out.println("Trainee #2 -> id=" + ta2.getUserId()
                    + ", username=" + ta2.getUsername()
                    + ", address=Yerevan");

            System.out.println("Trainees count = " + traineeFacade.countTrainees());

            System.out.println("\n--- Create Training ---");
            var training = trainingFacade.createTraining(
                    ta1.getUserId(),
                    tr1.getUserId(),
                    "Morning Session",
                    TrainingType.YOGA,
                    LocalDateTime.now(),
                    60
            );

            System.out.println("Training -> id=" + training.getId()
                    + ", name=" + training.getTrainingName()
                    + ", type=" + training.getTrainingType()
                    + ", duration=" + training.getTrainingDuration());

            printCounts(trainerFacade, traineeFacade, trainingFacade, "Final counts");

            long accounts = trainerFacade.countTrainers() + traineeFacade.countTrainees();
            System.out.println("\nAccounts (trainers + trainees) = " + accounts);

            System.out.println("\n=== DEMO END ===");
        }
    }

    private static void printCounts(TrainerFacade trainerFacade,
                                    TraineeFacade traineeFacade,
                                    TrainingFacade trainingFacade,
                                    String title) {

        System.out.println("[" + title + "]");
        System.out.println("Trainers:  " + trainerFacade.countTrainers());
        System.out.println("Trainees:  " + traineeFacade.countTrainees());
        System.out.println("Trainings: " + trainingFacade.countTrainings());
    }
}