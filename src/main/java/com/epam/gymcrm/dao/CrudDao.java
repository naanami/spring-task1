package com.epam.gymcrm.dao;

import java.util.List;
import java.util.Optional;

public interface CrudDao<ID, T> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void deleteById(ID id);
    void deleteAll();
    long count();
}
