package com.cinema.repository;

import java.io.IOException;

public interface BasicFileRepository<T,ID> extends CrudRepository<T,ID> {
    void saveDataToFile() throws IOException;
}
