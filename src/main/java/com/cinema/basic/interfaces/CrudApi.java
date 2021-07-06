package com.cinema.basic.interfaces;

import java.util.List;

public interface CrudApi<T, ID> {
    void create(T model);

    List<T> getAll();

    T getById(ID id);

    void update(T model);

    void deleteById(ID id);
}
