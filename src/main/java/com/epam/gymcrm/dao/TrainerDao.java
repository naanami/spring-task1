package com.epam.gymcrm.dao;

import com.epam.gymcrm.domain.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TrainerDao implements CrudDao<UUID, Trainer>{

    private Map<UUID, Trainer> storage;

    @Autowired
    public void setStorage(@Qualifier("trainerStorage")Map<UUID, Trainer> storage){
        this.storage = storage;
    }

    @Override
    public Trainer save(Trainer entity) {
        storage.put(entity.getUserId(), entity);
        return entity;
    }

    @Override
    public Optional<Trainer> findById(UUID userId) {
        return Optional.ofNullable(storage.get(userId));
    }

    @Override
    public List<Trainer> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteById(UUID userId) {
        storage.remove(userId);
    }
    @Override
    public void deleteAll() {
        storage.clear();
    }

    @Override
    public long count() {
        return storage.size();
    }

}
