package com.cinema.service.genre.impl;

import com.cinema.model.Genre;
import com.cinema.repository.genre.GenreFileRepository;
import com.cinema.service.genre.GenreService;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreFileRepository genreFileRepository;

    @Override
    public Genre create(Genre model) {
        return genreFileRepository.create(model);
    }

    @Override
    public List<Genre> getAll() {
        return genreFileRepository.getAll();
    }

    @Override
    public Genre getById(UUID id) {
        return genreFileRepository.getById(id);
    }

    @Override
    public Genre update(Genre model) {
        return genreFileRepository.update(model);
    }

    @Override
    public Genre deleteById(UUID id) {
        return genreFileRepository.deleteById(id);
    }
}
