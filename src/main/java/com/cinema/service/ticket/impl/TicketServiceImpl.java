package com.cinema.service.ticket.impl;

import com.cinema.model.Movie;
import com.cinema.model.Ticket;
import com.cinema.model.User;
import com.cinema.repository.ticket.TicketRepository;
import com.cinema.service.ticket.TicketService;
import lombok.RequiredArgsConstructor;
import java.io.IOException;
import java.time.*;
import java.util.*;

@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {
    private final TicketRepository ticketRepository;

    @Override
    public void create(Ticket model) {
        ticketRepository.create(model);
    }

    @Override
    public List<Ticket> getAll() {
        return ticketRepository.getAll();
    }

    @Override
    public Ticket getById(UUID uuid) {
        return ticketRepository.getById(uuid);
    }

    @Override
    public void update(Ticket model) {
        ticketRepository.update(model);
    }

    @Override
    public void deleteById(UUID uuid) {
        ticketRepository.deleteById(uuid);
    }


    @Override
    public List<Movie> getMoviesByTicketForToday() {
        return ticketRepository.getMoviesByTicketForToday();
    }

    @Override
    public List<User> getUsersByMovieAndDate(Movie movie, LocalDate showDate) {
        return ticketRepository.getUsersByMovieAndDate(movie, showDate);
    }

    @Override
    public double getIncomeForMonth(int month, int year) {
        return ticketRepository.getIncomeForMonth(month, year);
    }

    @Override
    public Map<Movie, Long> getMovieRatingByTicketsAmount() {
        return ticketRepository.getMovieRatingByTicketsAmount();
    }

    @Override
    public Map<Movie, Long> getMovieRatingByTicketsAmountInAscThatLess(long ticketsAmount) {
        return ticketRepository.getMovieRatingByTicketsAmountInAscThatLess(ticketsAmount);
    }

    @Override
    public void synchronize() throws IOException {
        ticketRepository.synchronize();
    }

}