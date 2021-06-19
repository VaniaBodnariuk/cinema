package com.cinema.service.ticket.impl;

import com.cinema.model.Movie;
import com.cinema.model.Ticket;
import com.cinema.model.User;
import com.cinema.repository.ticket.TicketFileRepository;
import com.cinema.service.ticket.TicketService;
import lombok.RequiredArgsConstructor;
import java.time.*;
import java.util.*;
import static java.time.LocalDate.of;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.util.Comparator.reverseOrder;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.*;

@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {
    private final TicketFileRepository ticketFileRepository;

    @Override
    public Ticket create(Ticket model) {
        return ticketFileRepository.create(model);
    }

    @Override
    public List<Ticket> getAll() {
        return ticketFileRepository.getAll();
    }

    @Override
    public Ticket getById(UUID uuid) {
        return ticketFileRepository.getById(uuid);
    }

    @Override
    public Ticket update(Ticket model) {
        return ticketFileRepository.update(model);
    }

    @Override
    public Ticket deleteById(UUID uuid) {
        return ticketFileRepository.deleteById(uuid);
    }

    @Override
    public List<Movie> getMoviesByTicketForToday() {
        return getAll().stream()
                .filter(this::checkTicketForToday)
                .map(Ticket::getMovie)
                .collect(toList());
    }

    @Override
    public List<User> getUsersByMovieAndDate(Movie movie,
                                             LocalDate showDate) {
        return getAll().stream()
                .filter(ticket -> checkUsersByMovieAndDate(ticket,
                                                           movie,
                                                           showDate)
                )
                .map(Ticket::getUser)
                .collect(toList());
    }

    @Override
    public double getIncomeForCurrentMonth(int month, int year) {
        return getAll().stream()
                .filter(ticket ->
                        checkLocalDateTimeForEntryIntoMonthRange(
                                                 ticket.getDate(),
                                                 month, year)
                )
                .map(Ticket::getPrice)
                .reduce(0.0, Double::sum);
    }

    @Override
    public Map<Movie, Long> getMovieRatingByVisitsAmount() {
        Map<Movie, Long> moviesGroupedByVisitsAmount
                         = getMoviesGroupedByVisitsAmount();

        LinkedHashMap<Movie, Long> rating = new LinkedHashMap<>();
        moviesGroupedByVisitsAmount.entrySet()
                .stream()
                .sorted(comparingByValue(reverseOrder()))
                .forEachOrdered(x -> rating.put(x.getKey(), x.getValue()));
        return rating;
    }

    @Override
    public Map<Movie, Long> getMovieRatingByVisitsAmountInDescOrderThatIsLess(
                                                          long ticketsAmount) {
        Map<Movie, Long> mapFilteredByLimitTicketsAmount
                         = getMoviesFilteredByLimitTicketsAmount(
                                      getMoviesGroupedByVisitsAmount(),
                                      ticketsAmount);

        LinkedHashMap<Movie, Long> sortedMap = new LinkedHashMap<>();
        mapFilteredByLimitTicketsAmount.entrySet()
                .stream()
                .sorted(comparingByValue())
                .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
        return sortedMap;
    }

    private boolean checkUsersByMovieAndDate(Ticket ticket, Movie movie,
                                             LocalDate showDate){
        return checkTicketForCurrentDate(ticket, showDate)
                && checkTicketForCurrentMovie(ticket, movie);
    }

    private boolean checkLocalDateTimeForEntryIntoMonthRange(
                                          LocalDateTime date,
                                          int month,
                                          int year){
        return checkIfLocalDateTimeAfterCurrentMonth(date,
                                              month - 1,
                                                    year)
                && checkIfLocalDateTimeBeforeCurrentMonth(date,
                                                    month + 1,
                                                          year);
    }

    private Map<Movie, Long> getMoviesGroupedByVisitsAmount(){
        return ticketFileRepository.getAll()
                .stream()
                .collect(groupingBy(Ticket::getMovie, counting()));
    }

    private Map<Movie, Long> getMoviesFilteredByLimitTicketsAmount(
            Map<Movie, Long> moviesGroupedByTicketsAmount,
            long ticketsAmount) {
        return moviesGroupedByTicketsAmount.entrySet()
                .stream()
                .filter(a -> a.getValue().compareTo(ticketsAmount) < 0)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private boolean checkTicketForToday(Ticket ticket) {
        return checkTicketForCurrentDate(ticket, LocalDate.now());
    }

    private boolean checkTicketForCurrentDate(Ticket ticket, LocalDate date) {
        return ticket.getDate().toLocalDate().equals(date);
    }

    private boolean checkTicketForCurrentMovie(Ticket ticket, Movie movie){
        return ticket.getMovie().equals(movie);
    }

    private boolean checkIfLocalDateTimeAfterCurrentMonth(LocalDateTime date,
                                                          int month,
                                                          int year){
        return date.toLocalDate()
                .isAfter(of(year, month - 1, 1)
                                  .with(lastDayOfMonth()));
    }

    private boolean checkIfLocalDateTimeBeforeCurrentMonth(LocalDateTime date,
                                                           int month,
                                                           int year){
        return date.toLocalDate()
                   .isBefore(of(year, month - 1, 1));
    }
}