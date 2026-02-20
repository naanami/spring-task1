package com.epam.gymcrm.dao;

import com.epam.gymcrm.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserDao implements CrudDao<UUID, User>{

    private Map<UUID, User> storage;

    @Autowired
    public void setStorage(@Qualifier("userStorage")Map<UUID, User> storage) {
        this.storage = storage;
    }

    @Override
    public User save(User entity) {
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteById(UUID id) {
        storage.remove(id);
    }

    //helper methods for duplication

    public boolean existsByUsername(String username) {
        return storage.values().stream().anyMatch(u -> u.getUsername().equals(username));
    }

    public List<User> findByUsernamePrefix(String prefix){
        return storage.values().stream()
                .filter(u -> u.getUsername().startsWith(prefix))
                .toList();
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
