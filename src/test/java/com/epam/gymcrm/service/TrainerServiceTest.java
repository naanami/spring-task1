package com.epam.gymcrm.service;

import com.epam.gymcrm.dao.TrainerDao;
import com.epam.gymcrm.dao.UserDao;
import com.epam.gymcrm.entity.Trainer;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.entity.User;
import com.epam.gymcrm.dto.GeneratedCredentials;
import com.epam.gymcrm.util.CredentialsGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class TrainerServiceTest {

    private TrainerService trainerService;
    private TrainerDao trainerDao;

    @BeforeEach
    void setup() {
        Map<UUID, User> userStorage = new ConcurrentHashMap<>();
        Map<UUID, Trainer> trainerStorage = new ConcurrentHashMap<>();

        UserDao userDao = new UserDao();
        userDao.setStorage(userStorage);

        trainerDao = new TrainerDao();
        trainerDao.setStorage(trainerStorage);

        CredentialsGenerator generator = new CredentialsGenerator(userDao, 10);

        UserService userService = new UserService();
        userService.setUserDao(userDao);
        userService.setCredentialsGenerator(generator);

        trainerService = new TrainerService();
        trainerService.setUserService(userService);
        trainerService.setTrainerDao(trainerDao);
    }

    @Test
    void createTrainerProfileShouldCreateUserAndTrainer() {
        GeneratedCredentials creds =
                trainerService.createTrainerProfile("Mike", "Tyson", TrainingType.STRENGTH);

        Trainer trainer = trainerDao.findById(creds.getUserId()).orElseThrow();

        assertEquals(TrainingType.STRENGTH, trainer.getSpecialization());
    }

    @Test
    void updateTrainerProfileShouldUpdateSpecialization() {
        GeneratedCredentials creds =
                trainerService.createTrainerProfile("Mike", "Tyson", TrainingType.CARDIO);

        Trainer updated =
                trainerService.updateTrainerProfile(creds.getUserId(), TrainingType.YOGA);

        assertEquals(TrainingType.YOGA, updated.getSpecialization());
    }
    @Test
    void deleteAllTrainersAndCountShouldWork() {
        trainerService.createTrainerProfile("Lala", "Lulu", TrainingType.CARDIO);
        assertEquals(1, trainerDao.findAll().size());
        trainerService.deleteAllTrainers();
        assertEquals(0, trainerDao.findAll().size());

        assertEquals(0, trainerService.countTrainers());
    }

}
