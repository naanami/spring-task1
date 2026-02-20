package com.epam.gymcrm.service;

import com.epam.gymcrm.dao.TraineeDao;
import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.dto.GeneratedCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class TraineeService {

    private static final Logger log = LoggerFactory.getLogger(TraineeService.class);

    private TraineeDao traineeDao;
    private UserService userService;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public GeneratedCredentials createTraineeProfile(String firstName, String lastName,
                                                     LocalDate dateOfBirth, String address) {

        log.debug("Creating trainee profile for {} {}", firstName, lastName);

        GeneratedCredentials creds = userService.registerUser(firstName, lastName);

        Trainee trainee = new Trainee(creds.getUserId(), dateOfBirth, address);
        traineeDao.save(trainee);

        log.info("Trainee profile created: userId={}", creds.getUserId());

        return creds;
    }

    public Optional<Trainee> selectTraineeProfile(UUID userId) {
        log.debug("Selecting trainee profile: userId={}", userId);
        return traineeDao.findById(userId);
    }

    public Trainee updateTraineeAddress(UUID userId, String newAddress) {
        log.debug("Updating trainee address: userId={}", userId);

        Trainee trainee = traineeDao.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Trainee not found: {}", userId);
                    return new RuntimeException("Trainee not found");
                });

        trainee.setAddress(newAddress);
        traineeDao.save(trainee);

        log.info("Trainee address updated: userId={}", userId);

        return trainee;
    }

    public void deleteTraineeProfile(UUID userId) {
        log.info("Deleting trainee profile: userId={}", userId);
        traineeDao.deleteById(userId);
    }
    public long countTrainees() {
        return traineeDao.count();
    }

    public void deleteAllTrainees() {
        traineeDao.deleteAll();
    }

}
