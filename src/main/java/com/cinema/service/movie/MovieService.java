package com.cinema.service.movie;

import com.cinema.basic.interfaces.CrudApi;
import com.cinema.basic.interfaces.SaveToPersistenceApi;
import com.cinema.model.Movie;

import java.util.UUID;

public interface MovieService extends CrudApi<Movie, UUID>,
                                      SaveToPersistenceApi {
}
