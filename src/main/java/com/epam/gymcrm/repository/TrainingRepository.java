package com.epam.gymcrm.repository;

import com.epam.gymcrm.entity.Training;
import com.epam.gymcrm.entity.TrainingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TrainingRepository extends JpaRepository<Training, UUID> {

    @Query("""
        select t
        from Training t
        where t.trainee.user.username = :traineeUsername
          and t.trainingDate >= :from
          and t.trainingDate <= :to
          and (:trainerName is null
               or lower(concat(t.trainer.user.firstName, ' ', t.trainer.user.lastName)) like lower(concat('%', :trainerName, '%')))
          and (:trainingType is null or t.trainingType = :trainingType)
        order by t.trainingDate desc
    """)
    List<Training> findTraineeTrainings(
            @Param("traineeUsername") String traineeUsername,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("trainerName") String trainerName,
            @Param("trainingType") TrainingType trainingType
    );

    @Query("""
        select t
        from Training t
        where t.trainer.user.username = :trainerUsername
          and t.trainingDate >= :from
          and t.trainingDate <= :to
          and (:traineeName is null
               or lower(concat(t.trainee.user.firstName, ' ', t.trainee.user.lastName)) like lower(concat('%', :traineeName, '%')))
        order by t.trainingDate desc
    """)
    List<Training> findTrainerTrainings(
            @Param("trainerUsername") String trainerUsername,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("traineeName") String traineeName
    );
}