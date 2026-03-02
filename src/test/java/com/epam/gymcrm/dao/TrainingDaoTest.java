package com.epam.gymcrm.dao;

import com.epam.gymcrm.entity.Training;
import com.epam.gymcrm.entity.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class TrainingDaoTest {

    private TrainingDao trainingDao;

    @BeforeEach
    void setup() {
        Map<UUID, Training> storage = new ConcurrentHashMap<>();
        trainingDao = new TrainingDao();
        trainingDao.setStorage(storage);
    }

    @Test
    void saveFindAllDeleteFlow() {
        UUID id = UUID.randomUUID();

        Training t = new Training(
                id,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Session",
                TrainingType.CARDIO,
                LocalDateTime.of(2026, 1, 1, 10, 0),
                60
        );

        trainingDao.save(t);

        assertTrue(trainingDao.findById(id).isPresent());
        assertEquals(1, trainingDao.findAll().size());

        trainingDao.deleteById(id);
        assertTrue(trainingDao.findById(id).isEmpty());
    }
}
