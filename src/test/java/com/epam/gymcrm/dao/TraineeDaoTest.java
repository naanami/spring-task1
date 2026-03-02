package com.epam.gymcrm.dao;

import com.epam.gymcrm.entity.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class TraineeDaoTest {

    private TraineeDao traineeDao;

    @BeforeEach
    void setup() {
        Map<UUID, Trainee> storage = new ConcurrentHashMap<>();
        traineeDao = new TraineeDao();
        traineeDao.setStorage(storage);
    }

    @Test
    void saveFindAllDeleteFlow() {
        UUID id = UUID.randomUUID();
        traineeDao.save(new Trainee(id, LocalDate.of(2000, 1, 1), "Addr"));

        assertTrue(traineeDao.findById(id).isPresent());
        assertEquals(1, traineeDao.findAll().size());

        traineeDao.deleteById(id);
        assertTrue(traineeDao.findById(id).isEmpty());
    }
}
