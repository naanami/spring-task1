package com.epam.gymcrm.dao;

import com.epam.gymcrm.domain.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TraineeDao implements CrudDao<UUID, Trainee>{

    private Map<UUID, Trainee> storage;

    @Autowired
    public void setStorage(@Qualifier("traineeStorage")Map<UUID, Trainee> storage){
        this.storage = storage;
    }

    @Override
    public Trainee save(Trainee entity) {
        storage.put(entity.getUserId(), entity);
        return entity;
    }

    @Override
    public Optional<Trainee> findById(UUID userId) {
        return Optional.ofNullable(storage.get(userId));
    }

    @Override
    public List<Trainee> findAll() {
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
