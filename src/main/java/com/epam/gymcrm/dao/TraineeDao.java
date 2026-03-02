package com.epam.gymcrm.dao;

import com.epam.gymcrm.entity.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TraineeDao {

    private Map<UUID, Trainee> storage;

    @Autowired
    public void setStorage(@Qualifier("traineeStorage")Map<UUID, Trainee> storage){
        this.storage = storage;
    }

    public Trainee save(Trainee entity) {
        storage.put(entity.getUserId(), entity);
        return entity;
    }

    public Optional<Trainee> findById(UUID userId) {
        return Optional.ofNullable(storage.get(userId));
    }

    public List<Trainee> findAll() {
        return new ArrayList<>(storage.values());
    }

    public void deleteById(UUID userId) {
        storage.remove(userId);
    }
    public void deleteAll() {
        storage.clear();
    }

    public long count() {
        return storage.size();
    }

}
