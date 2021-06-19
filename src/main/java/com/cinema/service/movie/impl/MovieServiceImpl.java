package com.cinema.service.movie.impl;

import com.cinema.model.Movie;
import com.cinema.repository.movie.MovieFileRepository;
import com.cinema.service.movie.MovieService;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieFileRepository movieFileRepository;

    @Override
    public Movie create(Movie model) {
        return movieFileRepository.create(model);
    }

    @Override
    public List<Movie> getAll() {
        return movieFileRepository.getAll();
    }

    @Override
    public Movie getById(UUID uuid) {
        return movieFileRepository.getById(uuid);
    }

    @Override
    public Movie update(Movie model) {
        return movieFileRepository.update(model);
    }

    @Override
    public Movie deleteById(UUID uuid) {
        return movieFileRepository.deleteById(uuid);
    }
}
