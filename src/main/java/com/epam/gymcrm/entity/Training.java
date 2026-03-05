package com.epam.gymcrm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "training")
public class Training {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trainee_id", nullable = false)
    private Trainee trainee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    @Column(nullable = false)
    private String trainingName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrainingType trainingType;

    @Column(nullable = false)
    private LocalDateTime trainingDate;

    @Column(nullable = false)
    private int trainingDuration;

    protected Training() {}

    public Training(Trainee trainee,
                    Trainer trainer,
                    String trainingName,
                    TrainingType trainingType,
                    LocalDateTime trainingDate,
                    int trainingDuration) {

        this.trainee = trainee;
        this.trainer = trainer;
        this.trainingName = trainingName;
        this.trainingType = trainingType;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
    }

    public UUID getId() { return id; }
    public Trainee getTrainee() { return trainee; }
    public Trainer getTrainer() { return trainer; }
    public String getTrainingName() { return trainingName; }
    public TrainingType getTrainingType() { return trainingType; }
    public LocalDateTime getTrainingDate() { return trainingDate; }
    public int getTrainingDuration() { return trainingDuration; }
}