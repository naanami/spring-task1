package com.epam.gymcrm.dao;

import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class TrainerDaoTest {

    private TrainerDao trainerDao;

    @BeforeEach
    void setup() {
        Map<UUID, Trainer> storage = new ConcurrentHashMap<>();
        trainerDao = new TrainerDao();
        trainerDao.setStorage(storage);
    }

    @Test
    void saveFindAllDeleteFlow() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        trainerDao.save(new Trainer(id1, TrainingType.CARDIO));
        trainerDao.save(new Trainer(id2, TrainingType.YOGA));

        assertEquals(2, trainerDao.findAll().size());
        assertTrue(trainerDao.findById(id1).isPresent());

        trainerDao.deleteById(id1);
        assertTrue(trainerDao.findById(id1).isEmpty());
        assertEquals(1, trainerDao.findAll().size());
    }
}
