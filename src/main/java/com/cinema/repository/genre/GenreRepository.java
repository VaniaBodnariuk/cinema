package com.cinema.repository.genre;

import com.cinema.basic.interfaces.CrudApi;
import com.cinema.basic.interfaces.SaveToPersistenceApi;
import com.cinema.model.Genre;

import java.util.UUID;

public interface GenreRepository extends CrudApi<Genre, UUID>,
                                         SaveToPersistenceApi {
}
