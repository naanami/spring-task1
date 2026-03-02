package com.epam.gymcrm.service;

import com.epam.gymcrm.dao.TraineeDao;
import com.epam.gymcrm.dao.UserDao;
import com.epam.gymcrm.entity.Trainee;
import com.epam.gymcrm.entity.User;
import com.epam.gymcrm.dto.GeneratedCredentials;
import com.epam.gymcrm.util.CredentialsGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class TraineeServiceTest {

    private TraineeService traineeService;
    private TraineeDao traineeDao;

    @BeforeEach
    void setup() {
        Map<UUID, User> userStorage = new ConcurrentHashMap<>();
        Map<UUID, Trainee> traineeStorage = new ConcurrentHashMap<>();

        UserDao userDao = new UserDao();
        userDao.setStorage(userStorage);

        traineeDao = new TraineeDao();
        traineeDao.setStorage(traineeStorage);

        CredentialsGenerator generator = new CredentialsGenerator(userDao, 10);

        UserService userService = new UserService();
        userService.setUserDao(userDao);
        userService.setCredentialsGenerator(generator);

        traineeService = new TraineeService();
        traineeService.setTraineeDao(traineeDao);
        traineeService.setUserService(userService);
    }

    @Test
    void createTraineeProfileShouldCreateUserAndTrainee() {
        GeneratedCredentials creds = traineeService.createTraineeProfile(
                "Anna", "Brown",
                LocalDate.of(2000, 1, 1),
                "Yerevan"
        );

        Trainee trainee = traineeDao.findById(creds.getUserId()).orElseThrow();
        assertEquals("Yerevan", trainee.getAddress());
        assertEquals(LocalDate.of(2000, 1, 1), trainee.getDateOfBirth());
    }

    @Test
    void updateTraineeAddressShouldUpdate() {
        GeneratedCredentials creds = traineeService.createTraineeProfile(
                "Anna", "Brown",
                LocalDate.of(2000, 1, 1),
                "Old"
        );

        Trainee updated = traineeService.updateTraineeAddress(creds.getUserId(), "New");
        assertEquals("New", updated.getAddress());
    }

    @Test
    void updateTraineeAddressShouldThrowIfMissing() {
        assertThrows(RuntimeException.class,
                () -> traineeService.updateTraineeAddress(UUID.randomUUID(), "New"));
    }
}
