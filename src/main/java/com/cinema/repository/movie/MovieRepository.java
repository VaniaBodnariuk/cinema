package com.cinema.repository.movie;

import com.cinema.basic.interfaces.CrudApi;
import com.cinema.basic.interfaces.SaveToPersistenceApi;
import com.cinema.model.Genre;
import com.cinema.model.Movie;

import java.util.Set;
import java.util.UUID;

public interface MovieRepository extends CrudApi<Movie, UUID>,
                                         SaveToPersistenceApi {
    Set<Genre> getGenresByMovieId(UUID movieId);
}
