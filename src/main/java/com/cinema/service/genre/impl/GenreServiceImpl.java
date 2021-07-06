package com.cinema.service.genre.impl;

import com.cinema.model.Genre;
import com.cinema.repository.genre.GenreRepository;
import com.cinema.service.genre.GenreService;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    public void create(Genre model) {
        genreRepository.create(model);
    }

    @Override
    public List<Genre> getAll() {
        return genreRepository.getAll();
    }

    @Override
    public Genre getById(UUID id) {
        return genreRepository.getById(id);
    }

    @Override
    public void update(Genre model) {
        genreRepository.update(model);
    }

    @Override
    public void deleteById(UUID id) {
        genreRepository.deleteById(id);
    }

    @Override
    public void synchronize() throws IOException {
        genreRepository.synchronize();
    }
}
