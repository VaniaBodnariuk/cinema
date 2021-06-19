package com.cinema.repository.movie.impl;

import com.cinema.exception.NotFoundException;
import com.cinema.exception.UniqueFieldException;
import com.cinema.model.Movie;
import com.cinema.model.Ticket;
import com.cinema.repository.movie.MovieFileRepository;
import com.cinema.repository.ticket.TicketFileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

//TODO Include FileUtility and format code
public class MovieFileRepositoryImpl implements MovieFileRepository {
    private final File file;
    private final Map<UUID, Movie> localStorage;
    private final TicketFileRepository ticketFileRepository;

    public MovieFileRepositoryImpl(File file,
                                   TicketFileRepository ticketFileRepository)
            throws IOException {
        this.file = file;
        this.localStorage = initLocalStorage();
        this.ticketFileRepository = ticketFileRepository;
    }

    private Map<UUID, Movie> initLocalStorage() throws IOException {
        if(file.length() == 0){
            return new HashMap<>();
        }
        return getDataFromFileViaMap();
    }

    private Map<UUID, Movie> getDataFromFileViaMap() throws IOException{
        Map<UUID, Movie> dataMap = new HashMap<>();
        List<Movie> dataList = getDataFromFile();
        dataList.forEach(model -> dataMap.put(model.getId(),model));
        return dataMap;
    }

    private List<Movie> getDataFromFile() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return Arrays.asList(mapper.readValue(file, Movie[].class));
    }

    public void saveDataToFile() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter()
                .writeValue(file, localStorage.values());
    }

    @Override
    public Movie create(Movie model) {
        checkTitleAndProducerNameForUniqueness(model);
        localStorage.put(model.getId(),model);
        return localStorage.get(model.getId());
    }

    private void checkTitleAndProducerNameForUniqueness(Movie model) {
        if(localStorage.containsValue(model)){
            throw new UniqueFieldException(model.getClass().getName(),
                                           model.getId(),"title and producerName");
        }
    }


    @Override
    public List<Movie> getAll(){
        return new ArrayList<>(localStorage.values());
    }

    @Override
    public Movie getById(UUID id){
        checkIdForExisting(id);
        return localStorage.get(id);
    }

    private void checkIdForExisting(UUID id){
        if(!localStorage.containsKey(id)){
            throw new NotFoundException(Movie.class.getName(),id);
        }
    }

    @Override
    public Movie update(Movie model){
        checkIdForExisting(model.getId());
        checkTitleAndProducerNameForUniqueness(model);
        updateReferencesInTickets(model);
        localStorage.put(model.getId(),model);
        return getById(model.getId());
    }

    private Stream<Ticket> findReferencesInTickets(Movie model){
        return ticketFileRepository.getAll()
                .stream()
                .filter(ticket -> ticket.getMovie().equals(model));
    }

    private void updateReferencesInTickets(Movie model){
        findReferencesInTickets(model).forEach(ticket -> ticket.setMovie(model));
    }

    @Override
    public Movie deleteById(UUID id){
        checkIdForExisting(id);
        deleteRelatedTickets(getById(id));
        return localStorage.remove(id);
    }

    private void deleteRelatedTickets(Movie model){
        findReferencesInTickets(model).forEach(ticket ->
                    ticketFileRepository.deleteById(ticket.getId()));
    }
}
