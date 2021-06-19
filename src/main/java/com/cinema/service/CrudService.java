package com.cinema.service;

import java.util.List;

public interface CrudService<T, ID> {
    T create(T model);

    List<T> getAll();

    T getById(ID id);

    T update(T model);

    T deleteById(ID id);
}
