package com.cinema.repository.movie;

import com.cinema.model.Movie;
import com.cinema.repository.BasicFileRepository;
import java.util.UUID;

public interface MovieFileRepository extends BasicFileRepository<Movie, UUID> {
}
