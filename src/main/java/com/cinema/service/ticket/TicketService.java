package com.cinema.service.ticket;

import com.cinema.model.Movie;
import com.cinema.model.Ticket;
import com.cinema.model.User;
import com.cinema.service.CrudService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TicketService extends CrudService<Ticket, UUID> {
    List<Movie> getMoviesByTicketForToday();

    List<User> getUsersByMovieAndDate(Movie movie,
                                      LocalDate showDate);

    double getIncomeForCurrentMonth(int month, int year);

    Map<Movie, Long> getMovieRatingByVisitsAmount();

    Map<Movie, Long> getMovieRatingByVisitsAmountInDescOrderThatIsLess(
                                                     long ticketsAmount);
}
