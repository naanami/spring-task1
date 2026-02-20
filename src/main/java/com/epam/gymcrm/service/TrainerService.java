package com.epam.gymcrm.service;

import com.epam.gymcrm.dao.TrainerDao;
import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.dto.GeneratedCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class TrainerService {

    private static final Logger log = LoggerFactory.getLogger(TrainerService.class);

    private UserService userService;
    private TrainerDao trainerDao;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    public GeneratedCredentials createTrainerProfile(String firstName,
                                                     String lastName,
                                                     TrainingType specialization) {

        log.debug("Creating trainer profile for {} {}", firstName, lastName);

        GeneratedCredentials creds = userService.registerUser(firstName, lastName);

        Trainer trainer = new Trainer(creds.getUserId(), specialization);
        trainerDao.save(trainer);

        log.info("Trainer profile created: userId={}, specialization={}",
                creds.getUserId(), specialization);

        return creds;
    }

    public Optional<Trainer> selectTrainerProfile(UUID userId) {
        log.debug("Selecting trainer profile: userId={}", userId);
        return trainerDao.findById(userId);
    }

    public Trainer updateTrainerProfile(UUID userId, TrainingType newSpecialization) {
        log.info("Updating trainer specialization: userId={}, newSpecialization={}",
                userId, newSpecialization);

        Trainer updated = new Trainer(userId, newSpecialization);
        trainerDao.save(updated);

        return updated;
    }
    public long countTrainers() {
        return trainerDao.count();
    }

    public void deleteAllTrainers() {
        trainerDao.deleteAll();
    }

}
