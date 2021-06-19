package com.cinema.repository;

import java.util.List;

public interface CrudRepository<T, ID> {
    T create(T model);

    List<T> getAll();

    T getById(ID id);

    T update(T model);

    T deleteById(ID id);
}