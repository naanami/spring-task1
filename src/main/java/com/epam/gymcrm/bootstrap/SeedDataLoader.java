package com.epam.gymcrm.bootstrap;

import com.epam.gymcrm.entity.Trainee;
import com.epam.gymcrm.entity.Trainer;
import com.epam.gymcrm.entity.Training;
import com.epam.gymcrm.entity.TrainingType;
import com.epam.gymcrm.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Component
public class SeedDataLoader {

    private static final Logger log = LoggerFactory.getLogger(SeedDataLoader.class);

    @Value("${app.seed-user-file}")
    private String userFile;

    @Value("${app.seed-trainee-file}")
    private String traineeFile;

    @Value("${app.seed-trainer-file}")
    private String trainerFile;

    @Value("${app.seed-training-file}")
    private String trainingFile;

    private Map<UUID, User> userStorage;
    private Map<UUID, Trainer> trainerStorage;
    private Map<UUID, Trainee> traineeStorage;
    private Map<UUID, Training> trainingStorage;

    @Autowired
    public void setUserStorage(@Qualifier("userStorage") Map<UUID, User> userStorage) {
        this.userStorage = userStorage;
    }

    @Autowired
    public void setTrainerStorage(@Qualifier("trainerStorage") Map<UUID, Trainer> trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    @Autowired
    public void setTraineeStorage(@Qualifier("traineeStorage") Map<UUID, Trainee> traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    @Autowired
    public void setTrainingStorage(@Qualifier("trainingStorage") Map<UUID, Training> trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    @PostConstruct
    public void init() {
        loadUsers();
        loadTrainees();
        loadTrainers();
        loadTrainings();

        removeStandaloneUsers();

        log.info("Seed loaded: users={}, trainees={}, trainers={}, trainings={}",
                userStorage.size(), traineeStorage.size(), trainerStorage.size(), trainingStorage.size());
    }

    private void removeStandaloneUsers() {
        java.util.Set<UUID> allowed = new java.util.HashSet<>();
        allowed.addAll(traineeStorage.keySet());
        allowed.addAll(trainerStorage.keySet());

        userStorage.keySet().removeIf(id -> !allowed.contains(id));
    }

    @PreDestroy
    public void shutdown() {
        log.info("Application shutdown");
    }

    private void loadUsers() {
        readFile(userFile, line -> {
            String[] parts = line.split(",");
            UUID id = UUID.fromString(parts[0]);
            String firstName = parts[1];
            String lastName = parts[2];
            String username = parts[3];
            String password = parts[4];
            boolean active = Boolean.parseBoolean(parts[5]);

            userStorage.put(id, new User(id, firstName, lastName, username, password, active));
        });
    }

    private void loadTrainees() {
        readFile(traineeFile, line -> {
            String[] parts = line.split(",");
            UUID userId = UUID.fromString(parts[0]);
            LocalDate dob = LocalDate.parse(parts[1]);
            String address = parts[2];

            traineeStorage.put(userId, new Trainee(userId, dob, address));
        });
    }

    private void loadTrainers() {
        readFile(trainerFile, line -> {
            String[] parts = line.split(",");
            UUID userId = UUID.fromString(parts[0]);
            TrainingType type = TrainingType.valueOf(parts[1]);

            trainerStorage.put(userId, new Trainer(userId, type));
        });
    }

    private void loadTrainings() {
        readFile(trainingFile, line -> {
            String[] parts = line.split(",");
            UUID id = UUID.fromString(parts[0]);
            UUID traineeId = UUID.fromString(parts[1]);
            UUID trainerId = UUID.fromString(parts[2]);
            String name = parts[3];
            TrainingType type = TrainingType.valueOf(parts[4]);
            LocalDateTime date = LocalDateTime.parse(parts[5]);
            int duration = Integer.parseInt(parts[6]);

            trainingStorage.put(id,
                    new Training(id, traineeId, trainerId, name, type, date, duration));
        });
    }

    private void readFile(String fileName, LineProcessor processor) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new ClassPathResource(fileName).getInputStream(), StandardCharsets.UTF_8))) {

            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) {
                    processor.process(line);
                }
            }

        } catch (Exception e) {
            throw new IllegalStateException("Failed to load seed file: " + fileName, e);
        }
    }

    private interface LineProcessor {
        void process(String line);
    }
}
