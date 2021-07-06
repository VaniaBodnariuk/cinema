package com.cinema.repository.ticket;

import com.cinema.basic.interfaces.CrudApi;
import com.cinema.basic.interfaces.SaveToPersistenceApi;
import com.cinema.model.Movie;
import com.cinema.model.Ticket;
import com.cinema.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TicketRepository extends CrudApi<Ticket, UUID>,
                                          SaveToPersistenceApi {
    List<Movie> getMoviesByTicketForToday();

    List<User> getUsersByMovieAndDate(Movie movie,
                                      LocalDate showDate);

    double getIncomeForMonth(int month, int year);

    Map<Movie, Long> getMovieRatingByTicketsAmount();

    Map<Movie, Long> getMovieRatingByTicketsAmountInAscThatLess(
            long ticketsAmount);
}
