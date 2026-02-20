package com.epam.gymcrm.dao;

import com.epam.gymcrm.domain.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TrainingDao implements CrudDao<UUID, Training>{

    private Map<UUID, Training> storage;

    @Autowired
    public void setStorage(@Qualifier("trainingStorage")Map<UUID, Training> storage){
        this.storage = storage;
    }

    @Override
    public Training save(Training entity) {
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Training> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Training> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteById(UUID id) {
        storage.remove(id);
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
