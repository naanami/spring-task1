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
    join fetch t.trainer tr
    join fetch tr.user
    join fetch t.trainee tt
    join fetch tt.user
    where tt.user.username = :traineeUsername
      and (:from is null or t.trainingDate >= :from)
      and (:to is null or t.trainingDate <= :to)
      and (:trainerName is null
           or lower(concat(tr.user.firstName, ' ', tr.user.lastName))
              like lower(concat('%', :trainerName, '%')))
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
    join fetch t.trainer tr
    join fetch tr.user
    join fetch t.trainee tt
    join fetch tt.user
    where tr.user.username = :trainerUsername
      and (:from is null or t.trainingDate >= :from)
      and (:to is null or t.trainingDate <= :to)
      and (:traineeName is null
           or lower(concat(tt.user.firstName, ' ', tt.user.lastName))
              like lower(concat('%', :traineeName, '%')))
    order by t.trainingDate desc
""")
    List<Training> findTrainerTrainings(
            @Param("trainerUsername") String trainerUsername,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("traineeName") String traineeName
    );
}