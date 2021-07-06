package com.cinema.service.genre;

import com.cinema.basic.interfaces.CrudApi;
import com.cinema.basic.interfaces.SaveToPersistenceApi;
import com.cinema.model.Genre;

import java.util.UUID;

public interface GenreService extends CrudApi<Genre,UUID>,
                                      SaveToPersistenceApi {
}
