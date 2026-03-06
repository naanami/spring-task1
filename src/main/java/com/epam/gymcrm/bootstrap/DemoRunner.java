package com.epam.gymcrm.bootstrap;

import com.epam.gymcrm.dto.GeneratedCredentials;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.facade.TraineeFacade;
import com.epam.gymcrm.facade.TrainerFacade;
import com.epam.gymcrm.facade.TrainingFacade;
import com.epam.gymcrm.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Scanner;

@Component
public class DemoRunner implements CommandLineRunner {

    private final TrainerFacade trainerFacade;
    private final TraineeFacade traineeFacade;
    private final TrainingFacade trainingFacade;
    private final UserService userService;

    public DemoRunner(TrainerFacade trainerFacade,
                      TraineeFacade traineeFacade,
                      TrainingFacade trainingFacade,
                      UserService userService) {
        this.trainerFacade = trainerFacade;
        this.traineeFacade = traineeFacade;
        this.trainingFacade = trainingFacade;
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n==============================");
        System.out.println("   GYM CRM DEMO (Hibernate)");
        System.out.println("==============================\n");

        System.out.println("1) Duplicate username generation:");

        GeneratedCredentials tr1 = trainerFacade.createTrainerProfile("John", "Doe", TrainingType.YOGA);
        GeneratedCredentials tr2 = trainerFacade.createTrainerProfile("John", "Doe", TrainingType.CARDIO);
        GeneratedCredentials tr3 = trainerFacade.createTrainerProfile("John", "Doe", TrainingType.PILATES);

        System.out.println("   Trainer #1 username: " + tr1.getUsername());
        System.out.println("   Trainer #2 username: " + tr2.getUsername());
        System.out.println("   Trainer #3 username: " + tr3.getUsername());

        String authUsername = tr1.getUsername();
        String authPassword = tr1.getPassword();

        System.out.println("\n2) Auth-required operations (using Trainer #1 credentials):");
        System.out.println("   Auth user: " + authUsername);

        System.out.println("\n3) Create trainees:");

        GeneratedCredentials ta1 = traineeFacade.createTraineeProfile(
                "Anna", "Smith",
                LocalDate.of(2000, 5, 10),
                "Yerevan"
        );

        GeneratedCredentials ta2 = traineeFacade.createTraineeProfile(
                "Anna", "Smith",
                LocalDate.of(2000, 5, 10),
                "Gyumri"
        );

        System.out.println("   Trainee #1 username: " + ta1.getUsername() + " (address=Yerevan)");
        System.out.println("   Trainee #2 username: " + ta2.getUsername() + " (address=Gyumri)");

        System.out.println("\n4) Create trainings:");

        LocalDateTime now = LocalDateTime.now();

        var trainingA = trainingFacade.createTraining(
                authUsername,
                authPassword,
                ta1.getUserId(),
                tr1.getUserId(),
                "Morning Yoga",
                TrainingType.YOGA,
                now.minusDays(2),
                60
        );

        var trainingB = trainingFacade.createTraining(
                authUsername,
                authPassword,
                ta1.getUserId(),
                tr2.getUserId(),
                "Cardio Blast",
                TrainingType.CARDIO,
                now.minusDays(1),
                45
        );

        System.out.println("   Created training A: " + trainingA.getId() + " (YOGA, -2 days)");
        System.out.println("   Created training B: " + trainingB.getId() + " (CARDIO, -1 day)");

        System.out.println("\n5) Training filters (trainee trainings):");
        var filtered = traineeFacade.getTraineeTrainings(
                authUsername,
                authPassword,
                ta1.getUsername(),
                now.minusDays(30),
                now.plusDays(1),
                "John Doe",
                TrainingType.YOGA
        );
        System.out.println("   Filtered trainings found (type=YOGA, trainer contains 'John Doe'): " + filtered.size());

        System.out.println("\n6) Not assigned trainers for trainee:");
        var notAssigned = traineeFacade.getNotAssignedTrainers(
                authUsername,
                authPassword,
                ta1.getUsername()
        );
        System.out.println("   Not assigned trainers count: " + notAssigned.size());

        System.out.println("\n7) Toggle active + change password:");
        userService.toggleActive(ta1.getUsername());
        System.out.println("   Toggled active for trainee username: " + ta1.getUsername());

        userService.changePassword(authUsername, authPassword, "NewPass1234");
        authPassword = "NewPass1234";
        System.out.println("   Changed password for auth user (not printed).");

        long before = trainingFacade.countTrainings(authUsername, authPassword);
        System.out.println("\n8) Pause BEFORE cascade delete:");
        System.out.println("   Trainings count before deleting trainee: " + before);
        System.out.println("   Check DB now: TRAINING table should contain 2 rows.");
        System.out.println("   Press ENTER to continue to cascade delete...");
        scanner.nextLine();

        System.out.println("\n9) Cascade delete demo (delete trainee -> trainings deleted):");
        traineeFacade.deleteTraineeProfile(authUsername, authPassword, ta1.getUserId());
        System.out.println("   Deleted trainee profile: " + ta1.getUsername());

        long after = trainingFacade.countTrainings(authUsername, authPassword);
        System.out.println("   Trainings count after deleting trainee: " + after);
        System.out.println("   Expected: 0, because trainings were removed by cascade.");

        System.out.println("\n10) Pause AFTER cascade delete:");
        System.out.println("    Check DB again: TRAINING table should now be empty.");
        System.out.println("    Press ENTER to exit...");
        scanner.nextLine();

        System.out.println("\n==============================");
        System.out.println("         DEMO END");
        System.out.println("==============================");
    }
}