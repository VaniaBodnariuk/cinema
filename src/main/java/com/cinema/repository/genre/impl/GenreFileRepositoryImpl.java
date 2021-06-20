package com.cinema.repository.genre.impl;

import com.cinema.exception.UniqueFieldException;
import com.cinema.exception.NotFoundException;
import com.cinema.model.Genre;
import com.cinema.model.Movie;
import com.cinema.repository.genre.GenreFileRepository;
import com.cinema.repository.movie.MovieFileRepository;
import com.cinema.utility.file.basic.FileUtility;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class GenreFileRepositoryImpl implements GenreFileRepository {
    private final FileUtility<Genre> fileUtility;
    private final Map<UUID, Genre> localStorage;
    private final MovieFileRepository movieFileRepository;

    public GenreFileRepositoryImpl(FileUtility<Genre> fileUtility,
                                   MovieFileRepository movieFileRepository)
            throws IOException {
        this.fileUtility = fileUtility;
        this.localStorage = initLocalStorage();
        this.movieFileRepository = movieFileRepository;
    }

    @Override
    public Genre create(Genre model) {
        checkNameForUniqueness(model);
        save(model);
        return getById(model.getId());
    }

    @Override
    public List<Genre> getAll(){
        return new ArrayList<>(localStorage.values());
    }

    @Override
    public Genre getById(UUID id){
        checkIdForExisting(id);
        return localStorage.get(id);
    }

    @Override
    public Genre update(Genre model){
        checkIdForExisting(model.getId());
        checkNameForUniqueness(model);
        updateReferencesInMovies(model);
        save(model);
        return getById(model.getId());
    }

    @Override
    public Genre deleteById(UUID id){
        checkIdForExisting(id);
        deleteReferencesInMovies(getById(id));
        return localStorage.remove(id);
    }

    @Override
    public void saveDataToFile() throws IOException {
        fileUtility.write(new ArrayList<>(localStorage.values()));
    }

    private Map<UUID, Genre> initLocalStorage() throws IOException {
        return (fileUtility.getFile().length() == 0)
                ? new HashMap<>()
                : getDataFromFileViaMap();
    }

    private void save(Genre model){
        localStorage.put(model.getId(), model);
    }

    private void checkNameForUniqueness(Genre model) {
        if(localStorage.containsValue(model)){
            throw new UniqueFieldException(model.getClass().getName(),
                                           model.getId(), "name");
        }
    }

    private void checkIdForExisting(UUID id){
        if(!localStorage.containsKey(id)){
            throw new NotFoundException(Genre.class.getName(), id);
        }
    }

    private void updateReferencesInMovies(Genre model){
        findReferencesInMovies(model).forEach(movie -> movie.getGenres()
                                                            .add(model));
    }

    private void deleteReferencesInMovies(Genre model){
        findReferencesInMovies(model).forEach(movie -> movie.getGenres()
                                                            .remove(model));
    }

    private Map<UUID, Genre> getDataFromFileViaMap() throws IOException{
        List<Genre> dataList = fileUtility.read();
        return convertDataListToDataMap(dataList);
    }

    private Stream<Movie> findReferencesInMovies(Genre model){
        return movieFileRepository.getAll()
                .stream()
                .filter(movie -> movie.getGenres().contains(model));
    }

    private Map<UUID, Genre> convertDataListToDataMap(List<Genre> dataList){
        Map<UUID,Genre> dataMap = new HashMap<>();
        dataList.forEach(genre -> dataMap.put(genre.getId(), genre));
        return dataMap;
    }
}
