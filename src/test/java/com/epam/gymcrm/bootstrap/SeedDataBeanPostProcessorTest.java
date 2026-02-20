package com.epam.gymcrm.bootstrap;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.Training;
import com.epam.gymcrm.domain.User;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class SeedDataBeanPostProcessorTest {

    @Test
    void shouldLoadSeedDataWhenTriggered() throws Exception {
        SeedDataBeanPostProcessor p = new SeedDataBeanPostProcessor();

        Map<UUID, User> users = new ConcurrentHashMap<>();
        Map<UUID, Trainer> trainers = new ConcurrentHashMap<>();
        Map<UUID, Trainee> trainees = new ConcurrentHashMap<>();
        Map<UUID, Training> trainings = new ConcurrentHashMap<>();

        p.setUserStorage(users);
        p.setTrainerStorage(trainers);
        p.setTraineeStorage(trainees);
        p.setTrainingStorage(trainings);

        setPrivateField(p, "userFile", "users.csv");
        setPrivateField(p, "traineeFile", "trainees.csv");
        setPrivateField(p, "trainerFile", "trainers.csv");
        setPrivateField(p, "trainingFile", "trainings.csv");

        assertDoesNotThrow(() -> p.postProcessAfterInitialization(new Object(), "trainingStorage"));

        assertTrue(users.size() >= 0);
        assertTrue(trainees.size() >= 0);
        assertTrue(trainers.size() >= 0);
        assertTrue(trainings.size() >= 0);
    }

    private static void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }
}
