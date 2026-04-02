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

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class TraineeCascadeDeleteTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void deletingTraineeShouldCascadeDeleteTrainings() {
        User trainerUser = userRepository.save(
                new User("John", "Doe", "John.Doe", "pass", true)
        );

        User traineeUser = userRepository.save(
                new User("Anna", "Smith", "Anna.Smith", "pass", true)
        );

        Trainer trainer = trainerRepository.save(
                new Trainer(trainerUser, TrainingType.YOGA)
        );

        Trainee trainee = traineeRepository.save(
                new Trainee(traineeUser, LocalDate.of(2000, 1, 1), "Addr")
        );

        Training training = trainingRepository.save(
                new Training(
                        trainee,
                        trainer,
                        "Morning Yoga",
                        TrainingType.YOGA,
                        LocalDateTime.of(2026, 3, 20, 9, 0),
                        60
                )
        );

        entityManager.flush();
        entityManager.clear();

        assertEquals(1, trainingRepository.count());

        Training managedTraining = trainingRepository.findById(training.getId()).orElseThrow();
        assertEquals("Anna.Smith", managedTraining.getTrainee().getUser().getUsername());

        Trainee managedTrainee = traineeRepository.findById(trainee.getId()).orElseThrow();
        traineeRepository.delete(managedTrainee);

        entityManager.flush();
        entityManager.clear();

        assertEquals(0, trainingRepository.count());
    }
}