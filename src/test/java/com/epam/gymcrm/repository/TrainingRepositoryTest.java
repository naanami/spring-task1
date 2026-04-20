package com.epam.gymcrm.repository;

import com.epam.gymcrm.entity.Trainee;
import com.epam.gymcrm.entity.Trainer;
import com.epam.gymcrm.entity.Training;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class TrainingRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findTraineeTrainingsShouldFilterCorrectly() {
        User trainerUser = userRepository.save(new User("John", "Doe", "John.Doe", "pass", true));
        User traineeUser = userRepository.save(new User("Anna", "Smith", "Anna.Smith", "pass", true));

        Trainer trainer = trainerRepository.save(new Trainer(trainerUser, TrainingType.YOGA));
        Trainee trainee = traineeRepository.save(new Trainee(traineeUser, LocalDate.of(2000, 1, 1), "Addr"));

        LocalDateTime yogaTime = LocalDateTime.of(2026, 3, 20, 9, 0);
        LocalDateTime cardioTime = LocalDateTime.of(2026, 3, 20, 11, 0);

        trainingRepository.save(new Training(
                trainee, trainer, "Morning Yoga",
                TrainingType.YOGA,
                yogaTime,
                60
        ));

        trainingRepository.save(new Training(
                trainee, trainer, "Cardio Session",
                TrainingType.CARDIO,
                cardioTime,
                45
        ));

        entityManager.flush();
        entityManager.clear();

        List<Training> result = trainingRepository.findTraineeTrainings(
                "Anna.Smith",
                LocalDateTime.of(2026, 3, 1, 0, 0),
                LocalDateTime.of(2026, 3, 31, 23, 59),
                "John Doe",
                TrainingType.YOGA
        );

        assertEquals(1, result.size());
        assertEquals("Morning Yoga", result.get(0).getTrainingName());
    }

    @Test
    void findTrainerTrainingsShouldFilterCorrectly() {
        User trainerUser = userRepository.save(new User("John", "Doe", "John.Doe", "pass", true));
        User traineeUser = userRepository.save(new User("Anna", "Smith", "Anna.Smith", "pass", true));

        Trainer trainer = trainerRepository.save(new Trainer(trainerUser, TrainingType.YOGA));
        Trainee trainee = traineeRepository.save(new Trainee(traineeUser, LocalDate.of(2000, 1, 1), "Addr"));

        trainingRepository.save(new Training(
                trainee, trainer, "Morning Yoga",
                TrainingType.YOGA,
                LocalDateTime.of(2026, 3, 20, 9, 0),
                60
        ));

        entityManager.flush();
        entityManager.clear();

        List<Training> result = trainingRepository.findTrainerTrainings(
                "John.Doe",
                LocalDateTime.of(2026, 3, 1, 0, 0),
                LocalDateTime.of(2026, 3, 31, 23, 59),
                "Anna Smith"
        );

        assertEquals(1, result.size());
    }

    @Test
    void findTrainerTrainingsShouldWorkWithNullDatesAndNullName() {
        User trainerUser = userRepository.save(new User("John", "Doe", "John.Doe", "pass", true));
        User traineeUser = userRepository.save(new User("Anna", "Smith", "Anna.Smith", "pass", true));

        Trainer trainer = trainerRepository.save(new Trainer(trainerUser, TrainingType.YOGA));
        Trainee trainee = traineeRepository.save(new Trainee(traineeUser, LocalDate.of(2000, 1, 1), "Addr"));

        trainingRepository.save(new Training(
                trainee, trainer, "Morning Yoga",
                TrainingType.YOGA,
                LocalDateTime.of(2026, 3, 20, 9, 0),
                60
        ));

        entityManager.flush();
        entityManager.clear();

        List<Training> result = trainingRepository.findTrainerTrainings(
                "John.Doe",
                null,
                null,
                null
        );

        assertEquals(1, result.size());
    }

    @Test
    void findTraineeTrainingsShouldWorkWithNullDatesAndNullTrainerName() {
        User trainerUser = userRepository.save(new User("John", "Doe", "John.Doe", "pass", true));
        User traineeUser = userRepository.save(new User("Anna", "Smith", "Anna.Smith", "pass", true));

        Trainer trainer = trainerRepository.save(new Trainer(trainerUser, TrainingType.YOGA));
        Trainee trainee = traineeRepository.save(new Trainee(traineeUser, LocalDate.of(2000, 1, 1), "Addr"));

        trainingRepository.save(new Training(
                trainee, trainer, "Morning Yoga",
                TrainingType.YOGA,
                LocalDateTime.of(2026, 3, 20, 9, 0),
                60
        ));

        entityManager.flush();
        entityManager.clear();

        List<Training> result = trainingRepository.findTraineeTrainings(
                "Anna.Smith",
                null,
                null,
                null,
                TrainingType.YOGA
        );

        assertEquals(1, result.size());
    }

    @Test
    void findNotAssignedToTraineeShouldReturnOnlyUnassignedActiveTrainers() {
        User trainerUser1 = userRepository.save(new User("John", "Doe", "John.Doe", "pass", true));
        User trainerUser2 = userRepository.save(new User("Mike", "Jones", "Mike.Jones", "pass", true));
        User trainerUser3 = userRepository.save(new User("Inactive", "Trainer", "Inactive.Trainer", "pass", false));
        User traineeUser = userRepository.save(new User("Anna", "Smith", "Anna.Smith", "pass", true));

        Trainer trainer1 = trainerRepository.save(new Trainer(trainerUser1, TrainingType.YOGA));
        Trainer trainer2 = trainerRepository.save(new Trainer(trainerUser2, TrainingType.CARDIO));
        trainerRepository.save(new Trainer(trainerUser3, TrainingType.CARDIO));

        Trainee trainee = traineeRepository.save(new Trainee(traineeUser, LocalDate.of(2000, 1, 1), "Addr"));

        trainee.addTrainer(trainer1);
        traineeRepository.save(trainee);

        entityManager.flush();
        entityManager.clear();

        List<Trainer> result = trainerRepository.findActiveTrainersNotAssignedToTrainee("Anna.Smith");

        assertEquals(1, result.size());
        assertEquals("Mike.Jones", result.get(0).getUser().getUsername());
    }
}