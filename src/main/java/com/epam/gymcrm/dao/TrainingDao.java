package com.epam.gymcrm.dao;

import com.epam.gymcrm.entity.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TrainingDao{

    private Map<UUID, Training> storage;

    @Autowired
    public void setStorage(@Qualifier("trainingStorage")Map<UUID, Training> storage){
        this.storage = storage;
    }

    public Training save(Training entity) {
        storage.put(entity.getId(), entity);
        return entity;
    }

    public Optional<Training> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Training> findAll() {
        return new ArrayList<>(storage.values());
    }

    public void deleteById(UUID id) {
        storage.remove(id);
    }
    public void deleteAll() {
        storage.clear();
    }

    public long count() {
        return storage.size();
    }

}
