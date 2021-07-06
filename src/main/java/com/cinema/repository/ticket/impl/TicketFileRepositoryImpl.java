package com.cinema.repository.ticket.impl;

import com.cinema.exception.NotFoundException;
import com.cinema.model.Movie;
import com.cinema.model.Ticket;
import com.cinema.model.User;
import com.cinema.repository.ticket.TicketRepository;
import com.cinema.utility.file.basic.FileUtility;
import com.cinema.utility.validator.ValidatorUtility;
import java.time.LocalDate;
import java.util.*;
import static java.util.stream.Collectors.*;

public class TicketFileRepositoryImpl implements TicketRepository {
    private final Map<UUID, Ticket> localStorage;
    private final FileUtility<Ticket> fileUtility;

    public TicketFileRepositoryImpl(FileUtility<Ticket> fileUtility) {
        this.fileUtility = fileUtility;
        this.localStorage = getDataFromFileViaMap();
    }

    @Override
    public void create(Ticket model) {
        ValidatorUtility.validateModel(model);
        save(model);
    }

    @Override
    public List<Ticket> getAll(){
        return localStorage.values()
                .stream()
                .map(TicketFileRepositoryImpl::createCopy)
                .collect(toList());
    }

    private static Ticket createCopy(Ticket model){
        return Ticket.builder()
                .id(model.getId())
                .user(model.getUser())
                .movie(model.getMovie())
                .date(model.getDate())
                .price(model.getPrice())
                .build();
    }

    @Override
    public Ticket getById(UUID id){
        checkIdForExisting(id);
        return createCopy(localStorage.get(id));
    }

    @Override
    public void update(Ticket model){
        ValidatorUtility.validateModel(model);
        checkIdForExisting(model.getId());
        save(model);
    }

    @Override
    public void deleteById(UUID id){
        checkIdForExisting(id);
        localStorage.remove(id);
    }

    @Override
    public void synchronize() {
        fileUtility.write(new ArrayList<>(localStorage.values()));
    }


    private void save(Ticket model){
        localStorage.put(model.getId(),model);
    }

    private void checkIdForExisting(UUID id){
        if(!localStorage.containsKey(id)){
            throw new NotFoundException(Ticket.class.getName(),id);
        }
    }

    private Map<UUID, Ticket> getDataFromFileViaMap() {
        List<Ticket> dataList = fileUtility.read();
        return convertDataListToDataMap(dataList);
    }

    private Map<UUID, Ticket> convertDataListToDataMap(List<Ticket> dataList){
        Map<UUID,Ticket> dataMap = new HashMap<>();
        dataList.forEach(genre -> dataMap.put(genre.getId(), genre));
        return dataMap;
    }

    @Override
    public List<Movie> getMoviesByTicketForToday() {
        return null;
    }

    @Override
    public List<User> getUsersByMovieAndDate(Movie movie, LocalDate showDate) {
        return null;
    }

    @Override
    public double getIncomeForMonth(int month, int year) {
        return 0;
    }

    @Override
    public Map<Movie, Long> getMovieRatingByTicketsAmount() {
        return null;
    }

    @Override
    public Map<Movie, Long> getMovieRatingByTicketsAmountInAscThatLess(long ticketsAmount) {
        return null;
    }
}
