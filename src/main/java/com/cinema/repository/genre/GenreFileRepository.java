package com.cinema.repository.genre;

import com.cinema.model.Genre;
import com.cinema.repository.BasicFileRepository;
import java.util.UUID;

public interface GenreFileRepository extends BasicFileRepository<Genre, UUID> {
}
