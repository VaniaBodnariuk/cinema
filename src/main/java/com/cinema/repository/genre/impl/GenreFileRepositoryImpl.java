package com.cinema.repository.genre.impl;

import com.cinema.exception.UniqueFieldException;
import com.cinema.exception.NotFoundException;
import com.cinema.model.Genre;
import com.cinema.model.Movie;
import com.cinema.repository.genre.GenreRepository;
import com.cinema.repository.movie.MovieRepository;
import com.cinema.utility.file.basic.FileUtility;
import com.cinema.utility.validator.ValidatorUtility;
import java.util.*;
import java.util.stream.Stream;
import static java.util.stream.Collectors.toList;

public class GenreFileRepositoryImpl implements GenreRepository {
    private final FileUtility<Genre> fileUtility;
    private final Map<UUID, Genre> localStorage;
    private final MovieRepository movieRepository;

    public GenreFileRepositoryImpl(FileUtility<Genre> fileUtility,
                                   MovieRepository movieRepository) {
        this.fileUtility = fileUtility;
        this.localStorage = getDataFromFileViaMap();
        this.movieRepository = movieRepository;
    }

    @Override
    public void create(Genre model) {
        ValidatorUtility.validateModel(model);
        checkNameForUniqueness(model);
        save(model);
    }

    @Override
    public List<Genre> getAll(){
        return localStorage.values()
                .stream()
                .map(Genre::createCopy)
                .collect(toList());
    }

    @Override
    public Genre getById(UUID id){
        checkIdForExisting(id);
        return localStorage.get(id).createCopy();
    }

    @Override
    public void update(Genre model){
        ValidatorUtility.validateModel(model);
        Genre oldModel = getById(model.getId());
        if(!oldModel.equals(model)) {
            checkNameForUniqueness(model);
        }
        updateReferencesInMovies(model);
        save(model);
    }

    @Override
    public void deleteById(UUID id){
        checkIdForExisting(id);
        deleteReferencesInMovies(getById(id));
        localStorage.remove(id);
    }

    @Override
    public void synchronize() {
        fileUtility.write(new ArrayList<>(localStorage.values()));
    }

    private void save(Genre model){
        localStorage.put(model.getId(), model);
    }

    private void checkNameForUniqueness(Genre model) {
        if(localStorage.containsValue(model)) {
            throw new UniqueFieldException(model.getClass().getName(),
                                       model.getId(), "name");
        }
    }

    private void checkIdForExisting(UUID id){
        if(!localStorage.containsKey(id)) {
            throw new NotFoundException(Genre.class.getName(), id);
        }
    }

    private void updateReferencesInMovies(Genre model){
        findReferencesInMovies(model).forEach(movie ->
                updateReferenceInMovie(movie, model));
    }

    private void updateReferenceInMovie(Movie movie, Genre genre){
        movie.getGenres().add(genre);
        movieRepository.update(movie);
    }

    private void deleteReferenceInMovie(Movie movie, Genre genre){
        movie.getGenres().remove(genre);
        movieRepository.update(movie);
    }


    private void deleteReferencesInMovies(Genre model){
        findReferencesInMovies(model).forEach(movie ->
                deleteReferenceInMovie(movie, model));
    }

    private Map<UUID, Genre> getDataFromFileViaMap() {
        List<Genre> dataList = fileUtility.read();
        return convertDataListToDataMap(dataList);
    }

    private Stream<Movie> findReferencesInMovies(Genre model){
        return movieRepository.getAll()
                .stream()
                .filter(movie -> isMovieHasGenre(movie, model));
    }

    private boolean isMovieHasGenre(Movie movie, Genre requiredGenre){
        return movie.getGenres()
                .stream()
                .anyMatch(genre -> genre.getId().equals(
                                                 requiredGenre.getId()));
    }

    private Map<UUID, Genre> convertDataListToDataMap(List<Genre> dataList){
        Map<UUID,Genre> dataMap = new HashMap<>();
        dataList.forEach(genre -> dataMap.put(genre.getId(), genre));
        return dataMap;
    }
}
