package com.cinema.service.movie.impl;

import com.cinema.model.Movie;
import com.cinema.repository.movie.MovieRepository;
import com.cinema.service.movie.MovieService;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;

    @Override
    public void create(Movie model) {
        movieRepository.create(model);
    }

    @Override
    public List<Movie> getAll() {
        return movieRepository.getAll();
    }

    @Override
    public Movie getById(UUID uuid) {
        return movieRepository.getById(uuid);
    }

    @Override
    public void update(Movie model) {
        movieRepository.update(model);
    }

    @Override
    public void deleteById(UUID uuid) {
        movieRepository.deleteById(uuid);
    }

    @Override
    public void synchronize() throws IOException {
        movieRepository.synchronize();
    }
}
