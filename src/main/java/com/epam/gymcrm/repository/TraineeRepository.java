package com.epam.gymcrm.repository;

import com.epam.gymcrm.entity.Trainee;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TraineeRepository extends JpaRepository<Trainee, Long> {
    Optional<Trainee> findByUserId(UUID userId);

    Optional<Trainee> findByUserUsername(String username);

    boolean existsByUserFirstNameAndUserLastName(String firstName, String lastName);

    @EntityGraph(attributePaths = {"user", "trainers", "trainers.user"})
    Optional<Trainee> findDetailedByUserUsername(String username);


}