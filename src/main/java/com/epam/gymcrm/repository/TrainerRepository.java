package com.epam.gymcrm.repository;

import com.epam.gymcrm.entity.Trainer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    Optional<Trainer> findByUserId(UUID userId);

    Optional<Trainer> findByUserUsername(String username);

    boolean existsByUserFirstNameAndUserLastName(String firstName, String lastName);

    @EntityGraph(attributePaths = {"user", "trainees", "trainees.user"})
    Optional<Trainer> findDetailedByUserUsername(String username);


    @EntityGraph(attributePaths = {"user"})
    @Query("""
    select tr
    from Trainer tr
    where tr.user.active = true
      and tr.id not in (
          select ttr.id
          from Trainee t
          join t.trainers ttr
          where t.user.username = :traineeUsername
      )
""")
    List<Trainer> findNotAssignedToTrainee(@Param("traineeUsername") String traineeUsername);

}