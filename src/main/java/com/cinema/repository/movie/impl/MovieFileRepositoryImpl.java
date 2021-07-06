package com.cinema.repository.movie.impl;

import com.cinema.exception.NotFoundException;
import com.cinema.exception.UniqueFieldException;
import com.cinema.model.Genre;
import com.cinema.model.Movie;
import com.cinema.model.Ticket;
import com.cinema.repository.movie.MovieRepository;
import com.cinema.repository.ticket.TicketRepository;
import com.cinema.utility.file.basic.FileUtility;
import com.cinema.utility.validator.ValidatorUtility;
import java.util.*;
import java.util.stream.Stream;
import static java.util.stream.Collectors.toList;

public class MovieFileRepositoryImpl implements MovieRepository {
    private final FileUtility<Movie> fileUtility;
    private final Map<UUID, Movie> localStorage;
    private final TicketRepository ticketRepository;

    public MovieFileRepositoryImpl(FileUtility<Movie> fileUtility,
                                   TicketRepository ticketRepository) {
        this.fileUtility = fileUtility;
        this.localStorage = getDataFromFileViaMap();
        this.ticketRepository = ticketRepository;
    }

    @Override
    public void create(Movie model) {
        ValidatorUtility.validateModel(model);
        checkTitleAndProducerNameForUniqueness(model);
        save(model);
    }

    @Override
    public List<Movie> getAll(){
        return localStorage.values()
                .stream()
                .map(Movie::createCopy)
                .collect(toList());
    }

    @Override
    public Movie getById(UUID id){
        checkIdForExisting(id);
        return localStorage.get(id).createCopy();
    }

    @Override
    public void update(Movie model){
        ValidatorUtility.validateModel(model);
        Movie oldModel = getById(model.getId());
        if(!oldModel.equals(model)){
            checkTitleAndProducerNameForUniqueness(model);
        }
        updateReferencesInTickets(model);
        save(model);
    }

    @Override
    public void deleteById(UUID id){
        checkIdForExisting(id);
        deleteRelatedTickets(getById(id));
        localStorage.remove(id);
    }

    @Override
    public void synchronize() {
        fileUtility.write(new ArrayList<>(localStorage.values()));
    }

    @Override
    public Set<Genre> getGenresByMovieId(UUID movieId) {
        return getById(movieId).getGenres();
    }

    private void save(Movie model){
        localStorage.put(model.getId(), model);
    }

    private void checkIdForExisting(UUID id){
        if(!localStorage.containsKey(id)){
            throw new NotFoundException(Movie.class.getName(), id);
        }
    }

    private Map<UUID, Movie> getDataFromFileViaMap() {
        List<Movie> dataList = fileUtility.read();
        return convertDataListToDataMap(dataList);
    }

    private Map<UUID, Movie> convertDataListToDataMap(List<Movie> dataList) {
        Map<UUID,Movie> dataMap = new HashMap<>();
        dataList.forEach(movie -> dataMap.put(movie.getId(), movie));
        return dataMap;
    }

    private void checkTitleAndProducerNameForUniqueness(Movie model) {
        if(localStorage.containsValue(model)){
            throw new UniqueFieldException(model.getClass().getName(),
                    model.getId(),"title and producerName");
        }
    }

    private Stream<Ticket> findReferencesInTickets(Movie model){
        return ticketRepository.getAll()
                .stream()
                .filter(ticket -> ticket.getMovie().equals(model));
    }

    private void updateReferencesInTickets(Movie model){
        findReferencesInTickets(model).forEach(ticket ->
                updateReferenceInTicket(ticket,model)
        );
    }

    private void updateReferenceInTicket(Ticket ticket, Movie movie){
        ticket.setMovie(movie);
        ticketRepository.update(ticket);
    }

    private void deleteRelatedTickets(Movie model){
        findReferencesInTickets(model).forEach(ticket ->
                ticketRepository.deleteById(ticket.getId()));
    }
}
