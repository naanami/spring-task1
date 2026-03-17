package com.epam.gymcrm.bootstrap;

import com.epam.gymcrm.dto.GeneratedCredentials;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.facade.TraineeFacade;
import com.epam.gymcrm.facade.TrainerFacade;
import com.epam.gymcrm.facade.TrainingFacade;
import com.epam.gymcrm.service.UserService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

@Component
public class DemoRunner {

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

    public void runDemo() {
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

        String trainerAuthUsername = tr1.getUsername();
        String trainerAuthPassword = tr1.getPassword();

        System.out.println("\n2) Auth-required operations:");
        System.out.println("   Trainer auth user: " + trainerAuthUsername);

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

        System.out.println("\n4) Assign trainers to trainee:");
        traineeFacade.updateTraineeTrainers(
                ta1.getUsername(),
                ta1.getPassword(),
                List.of(tr1.getUsername(), tr2.getUsername())
        );
        System.out.println("   Assigned trainers to " + ta1.getUsername() + ": "
                + tr1.getUsername() + ", " + tr2.getUsername());

        System.out.println("\n5) Create trainings:");

        LocalDateTime now = LocalDateTime.now();

        var trainingA = trainingFacade.createTraining(
                trainerAuthUsername,
                trainerAuthPassword,
                ta1.getUserId(),
                tr1.getUserId(),
                "Morning Yoga",
                TrainingType.YOGA,
                now.minusDays(2),
                60
        );

        var trainingB = trainingFacade.createTraining(
                trainerAuthUsername,
                trainerAuthPassword,
                ta1.getUserId(),
                tr2.getUserId(),
                "Cardio Blast",
                TrainingType.CARDIO,
                now.minusDays(1),
                45
        );

        System.out.println("   Created training A: " + trainingA.getId() + " (YOGA, -2 days)");
        System.out.println("   Created training B: " + trainingB.getId() + " (CARDIO, -1 day)");

        System.out.println("\n6) Training filters (trainee view):");
        var filtered = traineeFacade.getTraineeTrainings(
                ta1.getUsername(),
                ta1.getPassword(),
                null,
                null,
                null,
                null
        );
        System.out.println("   Filtered trainings found (type=YOGA): " + filtered.size());

        System.out.println("\n7) Not assigned trainers for trainee:");
        var notAssigned = traineeFacade.getNotAssignedTrainers(
                ta1.getUsername(),
                ta1.getPassword()
        );
        System.out.println("   Not assigned trainers count: " + notAssigned.size());

        System.out.println("\n8) Own-profile authentication check:");
        try {
            traineeFacade.updateTraineeAddress(
                    trainerAuthUsername,
                    trainerAuthPassword,
                    "Hacked Address"
            );
            System.out.println("   John updated only his own profile scope.");
        } catch (Exception e) {
            System.out.println("   Blocked -> " + e.getMessage());
        }

        System.out.println("\n9) Toggle active + change password:");
        userService.toggleActive(ta1.getUsername());
        System.out.println("   Toggled active for trainee username: " + ta1.getUsername());

        userService.changePassword(trainerAuthUsername, trainerAuthPassword, "NewPass1234");
        trainerAuthPassword = "NewPass1234";
        System.out.println("   Changed password for trainer auth user (not printed).");

        long before = trainingFacade.countTrainings(trainerAuthUsername, trainerAuthPassword);
        System.out.println("\n10) Pause BEFORE cascade delete:");
        System.out.println("   Trainings count before deleting trainee: " + before);
        System.out.println("   Check DB now: TRAINING table should contain 2 rows.");
        System.out.println("   Press ENTER to continue to cascade delete...");
        scanner.nextLine();

        System.out.println("\n11) Cascade delete demo:");
        traineeFacade.deleteTraineeProfile(ta1.getUsername(), ta1.getPassword());
        System.out.println("   Deleted trainee profile: " + ta1.getUsername());

        long after = trainingFacade.countTrainings(trainerAuthUsername, trainerAuthPassword);
        System.out.println("   Trainings count after deleting trainee: " + after);
        System.out.println("   Expected: 0");

        System.out.println("\n12) Pause AFTER cascade delete:");
        System.out.println("    TRAINING table should now be empty.");
        System.out.println("    Press ENTER to exit...");
        scanner.nextLine();

        System.out.println("\n==============================");
        System.out.println("         DEMO END");
        System.out.println("==============================");
    }
}