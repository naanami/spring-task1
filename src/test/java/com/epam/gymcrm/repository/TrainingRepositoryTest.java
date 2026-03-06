package com.epam.gymcrm.repository;

import com.epam.gymcrm.entity.Trainee;
import com.epam.gymcrm.entity.Trainer;
import com.epam.gymcrm.entity.Training;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.entity.User;
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

    @Test
    void findTraineeTrainingsShouldFilterCorrectly() {
        User trainerUser = userRepository.save(new User("John", "Doe", "John.Doe", "pass", true));
        User traineeUser = userRepository.save(new User("Anna", "Smith", "Anna.Smith", "pass", true));

        Trainer trainer = trainerRepository.save(new Trainer(trainerUser, TrainingType.YOGA));
        Trainee trainee = traineeRepository.save(new Trainee(traineeUser, LocalDate.of(2000, 1, 1), "Addr"));

        trainingRepository.save(new Training(
                trainee, trainer, "Morning Yoga",
                TrainingType.YOGA,
                LocalDateTime.now().minusDays(1),
                60
        ));

        trainingRepository.save(new Training(
                trainee, trainer, "Cardio Session",
                TrainingType.CARDIO,
                LocalDateTime.now().minusDays(1),
                45
        ));

        List<Training> result = trainingRepository.findTraineeTrainings(
                "Anna.Smith",
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now().plusDays(1),
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
                LocalDateTime.now().minusDays(1),
                60
        ));

        List<Training> result = trainingRepository.findTrainerTrainings(
                "John.Doe",
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now().plusDays(1),
                "Anna Smith"
        );

        assertEquals(1, result.size());
    }

    @Test
    void findNotAssignedToTraineeShouldReturnUnassignedTrainers() {
        User trainerUser1 = userRepository.save(new User("John", "Doe", "John.Doe", "pass", true));
        User trainerUser2 = userRepository.save(new User("Mike", "Jones", "Mike.Jones", "pass", true));
        User traineeUser = userRepository.save(new User("Anna", "Smith", "Anna.Smith", "pass", true));

        Trainer trainer1 = trainerRepository.save(new Trainer(trainerUser1, TrainingType.YOGA));
        Trainer trainer2 = trainerRepository.save(new Trainer(trainerUser2, TrainingType.CARDIO));
        Trainee trainee = traineeRepository.save(new Trainee(traineeUser, LocalDate.of(2000, 1, 1), "Addr"));

        trainee.addTrainer(trainer1);
        traineeRepository.save(trainee);

        List<Trainer> result = trainerRepository.findNotAssignedToTrainee("Anna.Smith");

        assertEquals(1, result.size());
        assertEquals(TrainingType.CARDIO, result.get(0).getSpecialization());
    }
}