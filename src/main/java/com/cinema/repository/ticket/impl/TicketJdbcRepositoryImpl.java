package com.cinema.repository.ticket.impl;

import com.cinema.configuration.JdbcConfiguration;
import com.cinema.exception.DataException;
import com.cinema.exception.NotFoundException;
import com.cinema.mapper.MovieMapper;
import com.cinema.mapper.TicketMapper;
import com.cinema.mapper.UserMapper;
import com.cinema.model.Movie;
import com.cinema.model.Ticket;
import com.cinema.model.User;
import com.cinema.repository.ticket.TicketRepository;
import com.cinema.utility.validator.ValidatorUtility;
import java.io.IOException;
import java.rmi.UnexpectedException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class TicketJdbcRepositoryImpl implements TicketRepository {
    private final MovieMapper movieMapper;
    private final UserMapper userMapper;
    private final TicketMapper ticketMapper;

    private final String SQL_SELECT_ALL
            = "SELECT t.id,"
            + "       t.date,"
            + "       t.price,"
            + "       t.movie_id,"
            + "       m.producer_name as movie_producer_name,"
            + "       m.rating as movie_rating,"
            + "       m.title as movie_title,"
            + "       m.duration as movie_duration,"
            + "       t.users_id,"
            + "       u.name as user_name,"
            + "       u.phone as user_phone "
            + "FROM ticket t "
            + "JOIN movie m ON m.id = t.movie_id "
            + "JOIN users u ON u.id = t.users_id";

    private final String SQL_SELECT_BY_ID
            = "SELECT t.id,"
            + "       t.date,"
            + "       t.price,"
            + "       t.movie_id,"
            + "       m.producer_name as movie_producer_name,"
            + "       m.rating as movie_rating,"
            + "       m.title as movie_title,"
            + "       m.duration as movie_duration,"
            + "       t.users_id,"
            + "       u.name as user_name,"
            + "       u.phone as user_phone "
            + "FROM ticket t "
            + "JOIN movie m ON m.id = t.movie_id "
            + "JOIN users u ON u.id = t.users_id "
            + "WHERE t.id = ?";

    private final String SQL_UPDATE
            = "UPDATE public.ticket "
            + "SET movie_id = ?, users_id = ?, date = ?, price = ? "
            + "WHERE id = ?";

    private final String SQL_INSERT
            = "INSERT INTO public.ticket "
            + "(id, movie_id, users_id, date, price) "
            + "VALUES (?, ?, ?, ?, ?)";

    private final String SQL_DELETE_BY_ID = "DELETE FROM public.ticket WHERE id = ?";

    private final String SQL_SELECT_MOVIES_BY_TICKETS_FOR_TODAY
            = "SELECT DISTINCT(movie.*) "
            + "FROM public.ticket "
            + "JOIN public.movie ON movie.id = ticket.movie_id "
            + "WHERE date_part('month', ticket.date) = ? "
            + "AND date_part('year', ticket.date) = ? "
            + "AND date_part('day', ticket.date) = ?";

    private final String SQL_SELECT_USERS_BY_MOVIE_AND_DATE
            = "SELECT users.* "
            + "FROM public.ticket "
            + "JOIN users on users.id = ticket.users_id "
            + "WHERE ticket.movie_id = ? "
                   + "AND date_part('year',ticket.date) = ? "
                   + "AND date_part('month', ticket.date) = ? "
                   + "AND date_part('day', ticket.date) = ? ";

    private final String SQL_CALC_INCOME_FOR_MONTH
            = "SELECT SUM(price) as income "
            + "FROM public.ticket "
            + "WHERE date_part('year', date) = ? "
                   + "AND date_part('month', date) = ?";

    private final String SQL_GET_MOVIE_RATING_BY_TICKETS_AMOUNT
            = "SELECT movie.*, COUNT(ticket) tickets_amount "
            + "FROM public.ticket  "
            + "JOIN movie on movie.id = ticket.movie_id "
            + "GROUP BY ticket.movie_id "
            + "ORDER BY tickets_amount DESC";

    private final String SQL_GET_MOVIE_RATING_BY_TICKETS_AMOUNT_IN_ASC_THAT_LESS
            = "SELECT movie.*, COUNT(ticket) tickets_amount "
            + "FROM public.ticket "
            + "JOIN movie on movie.id = ticket.movie_id "
            + "GROUP BY ticket.movie_id "
            + "ORDER BY tickets_amount "
            + "LIMIT ?";

    public TicketJdbcRepositoryImpl(MovieMapper movieMapper, UserMapper userMapper, TicketMapper ticketMapper) {
        this.movieMapper = movieMapper;
        this.userMapper = userMapper;
        this.ticketMapper = ticketMapper;
    }

    @Override
    public List<Ticket> getAll() {
        try (Connection connection = JdbcConfiguration.getConnection();
             PreparedStatement preparedStatement
                     = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = preparedStatement.executeQuery()) {
            List<Ticket> models = new ArrayList<>();
            while (rs.next()) {
                models.add(ticketMapper.mapJdbcResultToModel(rs));
            }
            return models;
        } catch (SQLException e) {
            throw new DataException(e.getMessage(),e.getCause());
        }
    }

    @Override
    public Ticket getById(UUID uuid) {
        try (Connection connection = JdbcConfiguration.getConnection();
             PreparedStatement preparedStatement
                     = connection.prepareStatement(SQL_SELECT_BY_ID)) {
            preparedStatement.setObject(1, uuid);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                checkIdForExisting(rs.next(), uuid);
                return ticketMapper.mapJdbcResultToModel(rs);
            }
        } catch (SQLException e) {
            throw  new DataException(e.getMessage(),e.getCause());
        }
    }

    @Override
    public void create(Ticket model) {
        ValidatorUtility.validateModel(model);
        try (Connection connection = JdbcConfiguration.getConnection();
             PreparedStatement preparedStatement
                     = connection.prepareStatement(SQL_INSERT)) {
            preparedStatement.setObject(1, model.getId());
            preparedStatement.setObject(2, model.getMovie().getId());
            preparedStatement.setObject(3, model.getUser().getId());
            preparedStatement.setObject(4, model.getDate());
            preparedStatement.setDouble(5, model.getPrice());
            preparedStatement.executeUpdate();
        }catch (SQLException e) {
            throw  new DataException(e.getMessage(),e.getCause());
        }
    }

    @Override
    public void update(Ticket model) {
        ValidatorUtility.validateModel(model);
        try (Connection connection = JdbcConfiguration.getConnection();
             PreparedStatement preparedStatement
                     = connection.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setObject(1, model.getMovie().getId());
            preparedStatement.setObject(2, model.getUser().getId());
            preparedStatement.setObject(3, model.getDate());
            preparedStatement.setDouble(4, model.getPrice());
            preparedStatement.setObject(5, model.getId());
            preparedStatement.executeUpdate();
        }catch (SQLException e) {
            throw  new DataException(e.getMessage(),e.getCause());
        }
    }

    @Override
    public void deleteById(UUID uuid) {
        try (Connection connection = JdbcConfiguration.getConnection();
             PreparedStatement preparedStatement
                     = connection.prepareStatement(SQL_DELETE_BY_ID)) {
            preparedStatement.setObject(1, uuid);
            checkIdForExisting(preparedStatement.executeUpdate(), uuid);
        }catch (SQLException e) {
            throw new DataException(e.getMessage(),e.getCause());
        }
    }

    @Override
    public void synchronize() throws IOException {
        throw new UnexpectedException("");
    }

    @Override
    public List<Movie> getMoviesByTicketForToday() {
        try (Connection connection = JdbcConfiguration.getConnection();
             PreparedStatement preparedStatement
                     = connection.prepareStatement(
                             SQL_SELECT_MOVIES_BY_TICKETS_FOR_TODAY)) {
            LocalDate today = LocalDate.now();
            preparedStatement.setObject(1,today.getMonthValue());
            preparedStatement.setObject(2, today.getYear());
            preparedStatement.setObject(3, today.getDayOfMonth());
            List<Movie> movies = new ArrayList<>();
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    movies.add(movieMapper.mapJdbcResultToModel(rs));
                }
            }
            return movies;
        } catch (SQLException e) {
            throw  new DataException(e.getMessage(),e.getCause());
        }
    }

    @Override
    public List<User> getUsersByMovieAndDate(Movie movie,
                                      LocalDate showDate){
        try (Connection connection = JdbcConfiguration.getConnection();
             PreparedStatement preparedStatement
                     = connection.prepareStatement(
                     SQL_SELECT_USERS_BY_MOVIE_AND_DATE)) {
            preparedStatement.setObject(1, movie.getId());
            preparedStatement.setObject(2, showDate.getYear());
            preparedStatement.setObject(3, showDate.getMonthValue());
            preparedStatement.setObject(4, showDate.getDayOfMonth());
            List<User> users = new ArrayList<>();
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    users.add(userMapper.mapJdbcResultToModel(rs));
                }
            }
            return users;
        } catch (SQLException e) {
            throw  new DataException(e.getMessage(),e.getCause());
        }
    }

    @Override
    public double getIncomeForMonth(int month, int year){
        try (Connection connection = JdbcConfiguration.getConnection();
             PreparedStatement preparedStatement
                     = connection.prepareStatement(
                     SQL_CALC_INCOME_FOR_MONTH)) {
            preparedStatement.setObject(1, year);
            preparedStatement.setObject(2, month);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                rs.next();
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            throw  new DataException(e.getMessage(),e.getCause());
        }
    }

    @Override
    public Map<Movie, Long> getMovieRatingByTicketsAmount(){
        try (Connection connection = JdbcConfiguration.getConnection();
             PreparedStatement preparedStatement
                     = connection.prepareStatement(
                    SQL_GET_MOVIE_RATING_BY_TICKETS_AMOUNT)) {
            Map<Movie, Long> rating = new LinkedHashMap<>();
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    rating.put(movieMapper.mapJdbcResultToModel(rs), rs.getLong(2));
                }
            }
            return rating;
        } catch (SQLException e) {
            throw  new DataException(e.getMessage(),e.getCause());
        }
    }

    @Override
    public Map<Movie, Long> getMovieRatingByTicketsAmountInAscThatLess(
            long ticketsAmount) {
        try (Connection connection = JdbcConfiguration.getConnection();
             PreparedStatement preparedStatement
                     = connection.prepareStatement(
                     SQL_GET_MOVIE_RATING_BY_TICKETS_AMOUNT_IN_ASC_THAT_LESS)) {
            preparedStatement.setObject(1, ticketsAmount);
            Map<Movie, Long> rating = new LinkedHashMap<>();
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    rating.put(movieMapper.mapJdbcResultToModel(rs), rs.getLong(2));
                }
            }
            return rating;
        } catch (SQLException e) {
            throw  new DataException(e.getMessage(),e.getCause());
        }
    }

    private void checkIdForExisting(boolean isRsNext, UUID id) {
        if (!isRsNext)
            throw new NotFoundException(Movie.class.getSimpleName(), id);
    }

    private void checkIdForExisting(int executeUpdateResult, UUID id) {
        if (executeUpdateResult == 0)
            throw new NotFoundException(Movie.class.getSimpleName(), id);
    }
}



















