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
import static java.time.LocalDate.of;
import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.util.Comparator.reverseOrder;
import static java.util.Map.Entry.comparingByValue;
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

    @Override
    public List<Movie> getMoviesByTicketForToday() {
        return getAll().stream()
                .filter(this::isTicketForToday)
                .map(Ticket::getMovie)
                .distinct()
                .collect(toList());
    }

    @Override
    public List<User> getUsersByMovieAndDate(Movie movie,
                                             LocalDate showDate) {
        return getAll().stream()
                .filter(ticket -> isTicketForMovieAndDate(ticket,
                        movie,
                        showDate)
                )
                .map(Ticket::getUser)
                .collect(toList());
    }

    @Override
    public double getIncomeForMonth(int month, int year) {
        return getAll().stream()
                .filter(ticket ->
                        isDateIncludedInMonth(
                                ticket.getDate()
                                        .toLocalDate(),
                                month, year)
                )
                .map(Ticket::getPrice)
                .reduce(0.0, Double::sum);
    }

    @Override
    public Map<Movie, Long> getMovieRatingByTicketsAmount() {
        Map<Movie, Long> moviesWithTicketsAmount
                = getMoviesWithTicketsAmount();

        Map<Movie, Long> rating = new LinkedHashMap<>();
        moviesWithTicketsAmount.entrySet()
                .stream()
                .sorted(comparingByValue(reverseOrder()))
                .forEachOrdered(x -> rating.put(x.getKey(), x.getValue()));
        return rating;
    }

    @Override
    public Map<Movie, Long> getMovieRatingByTicketsAmountInAscThatLess(
            long ticketsAmount) {
        Map<Movie, Long> filteredMoviesWithTicketsAmount
                = getMoviesWithTicketsAmountThatLess(
                getMoviesWithTicketsAmount(),
                ticketsAmount);

        LinkedHashMap<Movie, Long> rating = new LinkedHashMap<>();
        filteredMoviesWithTicketsAmount.entrySet()
                .stream()
                .sorted(comparingByValue())
                .forEachOrdered(x -> rating.put(x.getKey(), x.getValue()));
        return rating;
    }

    private boolean isTicketForMovieAndDate(Ticket ticket, Movie movie,
                                            LocalDate showDate){
        return isTicketForDate(ticket, showDate)
                && isTicketForMovie(ticket, movie);
    }

    private boolean isDateIncludedInMonth(LocalDate date,
                                          int month,
                                          int year) {
        return isDateAfterMonth(date, month, year)
                && isDateBeforeMonth(date, month, year);
    }

    private Map<Movie, Long> getMoviesWithTicketsAmount(){
        return getAll().stream()
                .collect(groupingBy(Ticket::getMovie, counting()));
    }

    private Map<Movie, Long> getMoviesWithTicketsAmountThatLess(
            Map<Movie, Long> moviesWithTicketsAmount,
            long ticketsAmount) {
        return moviesWithTicketsAmount.entrySet()
                .stream()
                .filter(a -> a.getValue().compareTo(ticketsAmount) < 0)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private boolean isTicketForToday(Ticket ticket) {
        return isTicketForDate(ticket, LocalDate.now());
    }

    private boolean isTicketForDate(Ticket ticket, LocalDate date) {
        return ticket.getDate().toLocalDate().equals(date);
    }

    private boolean isTicketForMovie(Ticket ticket, Movie movie){
        return ticket.getMovie().equals(movie);
    }

    private boolean isDateAfterMonth(LocalDate date,
                                     int month,
                                     int year){
        return date.isAfter(of(year, month - 1, 1)
                .with(lastDayOfMonth()));
    }

    private boolean isDateBeforeMonth(LocalDate date,
                                      int month,
                                      int year){
        return date.isBefore(of(year, month + 1, 1)
                .with(firstDayOfMonth()));
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
}
