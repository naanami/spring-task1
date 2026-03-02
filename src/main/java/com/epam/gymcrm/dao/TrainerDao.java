package com.epam.gymcrm.dao;

import com.epam.gymcrm.entity.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TrainerDao{

    private Map<UUID, Trainer> storage;

    @Autowired
    public void setStorage(@Qualifier("trainerStorage")Map<UUID, Trainer> storage){
        this.storage = storage;
    }

    public Trainer save(Trainer entity) {
        storage.put(entity.getUserId(), entity);
        return entity;
    }

    public Optional<Trainer> findById(UUID userId) {
        return Optional.ofNullable(storage.get(userId));
    }

    public List<Trainer> findAll() {
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
