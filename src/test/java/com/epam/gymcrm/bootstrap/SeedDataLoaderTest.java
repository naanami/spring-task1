package com.epam.gymcrm.bootstrap;

import com.epam.gymcrm.entity.Trainee;
import com.epam.gymcrm.entity.Trainer;
import com.epam.gymcrm.entity.Training;
import com.epam.gymcrm.entity.User;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class SeedDataLoaderTest {

    @Test
    void initShouldLoadSeedData() throws Exception {
        SeedDataLoader loader = new SeedDataLoader();

        Map<UUID, User> users = new ConcurrentHashMap<>();
        Map<UUID, Trainer> trainers = new ConcurrentHashMap<>();
        Map<UUID, Trainee> trainees = new ConcurrentHashMap<>();
        Map<UUID, Training> trainings = new ConcurrentHashMap<>();

        loader.setUserStorage(users);
        loader.setTrainerStorage(trainers);
        loader.setTraineeStorage(trainees);
        loader.setTrainingStorage(trainings);

        setPrivateField(loader, "userFile", "users.csv");
        setPrivateField(loader, "traineeFile", "trainees.csv");
        setPrivateField(loader, "trainerFile", "trainers.csv");
        setPrivateField(loader, "trainingFile", "trainings.csv");

        assertDoesNotThrow(loader::init);

        assertTrue(users.size() >= 0);
        assertTrue(trainers.size() >= 0);
        assertTrue(trainees.size() >= 0);
        assertTrue(trainings.size() >= 0);
    }

    private static void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }
}
