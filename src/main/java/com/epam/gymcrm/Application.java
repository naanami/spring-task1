package com.epam.gymcrm;

import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.facade.TraineeFacade;
import com.epam.gymcrm.facade.TrainerFacade;
import com.epam.gymcrm.facade.TrainingFacade;
import com.epam.gymcrm.facade.UserFacade;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Application {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.epam.gymcrm");

        try {
            UserFacade userFacade = context.getBean(UserFacade.class);
            TrainerFacade trainerFacade = context.getBean(TrainerFacade.class);
            TraineeFacade traineeFacade = context.getBean(TraineeFacade.class);
            TrainingFacade trainingFacade = context.getBean(TrainingFacade.class);

            System.out.println("=== GYM CRM DEMO ===\n");

            printCounts(userFacade, trainerFacade, traineeFacade, trainingFacade, "Initial counts");

            System.out.println("\n--- Trainers reset + counter ---");
            trainerFacade.deleteAllTrainers();
            System.out.println("Deleted all trainers.");
            System.out.println("Trainers count = " + trainerFacade.countTrainers());

            var trainerCreds = trainerFacade.createTrainerProfile("Mona", "Ali", TrainingType.YOGA);
            System.out.println("Created 1 trainer -> id=" + trainerCreds.getUserId()
                    + ", username=" + trainerCreds.getUsername()
                    + ", specialization=" + TrainingType.YOGA);
            System.out.println("Trainers count = " + trainerFacade.countTrainers());

            System.out.println("\n--- Username uniqueness ---");
            userFacade.deleteAllUsers();
            System.out.println("Deleted all users.");
            System.out.println("Users count = " + userFacade.countUsers());

            var u1 = userFacade.registerUser("John", "Doe");
            var u2 = userFacade.registerUser("John", "Doe");
            var u3 = userFacade.registerUser("John", "Doe");

            System.out.println("Created again:");
            System.out.println("  John Doe #1 -> username=" + u1.getUsername());
            System.out.println("  John Doe #2 -> username=" + u2.getUsername());
            System.out.println("  John Doe #3 -> username=" + u3.getUsername());
            System.out.println("Users count = " + userFacade.countUsers());

            System.out.println("\n--- Trainee + Training ---");

            var traineeCreds = traineeFacade.createTraineeProfile(
                    "Anna",
                    "Smith",
                    LocalDate.of(2000, 5, 10),
                    "Yerevan"
            );

            System.out.println("Created trainee -> id=" + traineeCreds.getUserId()
                    + ", username=" + traineeCreds.getUsername()
                    + ", address=Yerevan");

            var training = trainingFacade.createTraining(
                    traineeCreds.getUserId(),
                    trainerCreds.getUserId(),
                    "Morning Session",
                    TrainingType.YOGA,
                    LocalDateTime.now(),
                    60
            );

            System.out.println("Created training -> id=" + training.getId()
                    + ", name=" + training.getTrainingName()
                    + ", type=" + training.getTrainingType()
                    + ", duration=" + training.getTrainingDuration());

            printCounts(userFacade, trainerFacade, traineeFacade, trainingFacade, "Final counts");

            System.out.println("\n=== DEMO END ===");

        } finally {
            context.close();
        }
    }

    private static void printCounts(UserFacade userFacade,
                                    TrainerFacade trainerFacade,
                                    TraineeFacade traineeFacade,
                                    TrainingFacade trainingFacade,
                                    String title) {

        System.out.println("[" + title + "]");
        System.out.println("Users:     " + userFacade.countUsers());
        System.out.println("Trainers:  " + trainerFacade.countTrainers());
        System.out.println("Trainees:  " + traineeFacade.countTrainees());
        System.out.println("Trainings: " + trainingFacade.countTrainings());
    }
}
