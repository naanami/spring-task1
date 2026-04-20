package com.epam.gymcrm.repository;

import com.epam.gymcrm.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    Optional<Trainer> findByUserUsername(String username);

    Optional<Trainer> findByUserId(java.util.UUID userId);

    boolean existsByUserFirstNameAndUserLastName(String firstName, String lastName);

    List<Trainer> findByUserUsernameIn(List<String> usernames);

    @Query("""
    select tr
    from Trainer tr
    join tr.user u
    where u.active = true
      and tr.id not in (
          select assignedTrainer.id
          from Trainee t
          join t.user tu
          join t.trainers assignedTrainer
          where tu.username = :username
      )
    """)
    List<Trainer> findActiveTrainersNotAssignedToTrainee(@Param("username") String username);

    @Query("""
select tr
from Trainer tr
join fetch tr.user
where tr.user.username = :username
""")
    Optional<Trainer> findDetailedByUserUsername(@Param("username") String username);
}