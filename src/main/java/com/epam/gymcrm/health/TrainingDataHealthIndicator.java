package com.epam.gymcrm.health;

import com.epam.gymcrm.repository.TrainingRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TrainingDataHealthIndicator implements HealthIndicator {

    private final TrainingRepository trainingRepository;

    public TrainingDataHealthIndicator(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    @Override
    public Health health() {
        try {
            long count = trainingRepository.count();

            if (count > 0) {
                return Health.up()
                        .withDetail("trainingsCount", count)
                        .build();
            } else {
                return Health.down()
                        .withDetail("trainingsCount", 0)
                        .build();
            }
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}