package com.cinema.repository.movie.impl;

import com.cinema.configuration.JdbcConfiguration;
import com.cinema.exception.DataException;
import com.cinema.exception.NotFoundException;
import com.cinema.exception.UniqueFieldException;
import com.cinema.mapper.GenreMapper;
import com.cinema.mapper.MovieMapper;
import com.cinema.model.Genre;
import com.cinema.model.Movie;
import com.cinema.repository.movie.MovieRepository;
import com.cinema.utility.validator.ValidatorUtility;
import java.io.IOException;
import java.rmi.UnexpectedException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MovieJdbcRepositoryImpl implements MovieRepository {
    private final GenreMapper genreMapper;
    private final MovieMapper movieMapper;

    private final String SQL_SELECT_GENRES_BY_MOVIE_ID
            = "SELECT genre.* "
            + "FROM public.movie_has_genre "
            + "JOIN genre on genre.id = movie_has_genre.genre_id "
            + "WHERE movie_id = ?";

    private final String SQL_SELECT_ALL = "SELECT * FROM public.movie";

    private final String SQL_SELECT_BY_ID
            = "SELECT * "
            + "FROM public.movie "
            + "WHERE id = ?";

    private final String SQL_INSERT
            = "INSERT INTO public.movie (id, title, producer_name, rating, duration) "
            + "VALUES (?, ?, ?, ?, ?)";

    private final String SQL_UPDATE
            = "UPDATE public.movie "
            + "SET title = ?, producer_name = ?, rating = ?, duration = ? "
            + "WHERE id = ?";

    private final String SQL_DELETE_BY_ID = "DELETE FROM public.movie WHERE id = ?";

    private final String SQL_SELECT_BY_TITLE_AND_PRODUCER_NAME
            = "SELECT * "
            + "FROM public.movie "
            + "WHERE title = ? AND producer_name = ?";

    public MovieJdbcRepositoryImpl(GenreMapper genreMapper, MovieMapper movieMapper) {
        this.genreMapper = genreMapper;
        this.movieMapper = movieMapper;
    }


    @Override
    public List<Movie> getAll() {
        try (Connection connection = JdbcConfiguration.getConnection();
             PreparedStatement preparedStatement
                     = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = preparedStatement.executeQuery()) {
            List<Movie> models = new ArrayList<>();
            while (rs.next()) {
                models.add(movieMapper.mapJdbcResultToModel(rs));
            }
            return models;
        } catch (SQLException e) {
            throw new DataException(e.getMessage(),e.getCause());
        }
    }

    @Override
    public Movie getById(UUID uuid) {
        try (Connection connection = JdbcConfiguration.getConnection();
             PreparedStatement preparedStatement
                     = connection.prepareStatement(SQL_SELECT_BY_ID)) {
            preparedStatement.setObject(1, uuid);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                checkIdForExisting(rs.next(), uuid);
                return movieMapper.mapJdbcResultToModel(rs);
            }
        } catch (SQLException e) {
            throw  new DataException(e.getMessage(),e.getCause());
        }
    }

    @Override
    public void create(Movie model) {
        ValidatorUtility.validateModel(model);
        checkTitleAndProducerNameForUniqueness(model);
        try (Connection connection = JdbcConfiguration.getConnection();
             PreparedStatement preparedStatement
                     = connection.prepareStatement(SQL_INSERT)) {
            preparedStatement.setObject(1, model.getId());
            preparedStatement.setString(2, model.getTitle());
            preparedStatement.setString(3, model.getProducerName());
            preparedStatement.setDouble(4, model.getRating());
            preparedStatement.setString(5, model.getDuration().toString());
            preparedStatement.executeUpdate();
        }catch (SQLException e) {
            throw  new DataException(e.getMessage(),e.getCause());
        }
    }

    @Override
    public void update(Movie model) {
        ValidatorUtility.validateModel(model);
        Movie oldModel = getById(model.getId());
        if(!oldModel.equals(model)){
            checkTitleAndProducerNameForUniqueness(model);
        }
        try (Connection connection = JdbcConfiguration.getConnection();
             PreparedStatement preparedStatement
                     = connection.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1, model.getTitle());
            preparedStatement.setString(2, model.getProducerName());
            preparedStatement.setDouble(3, model.getRating());
            preparedStatement.setString(4, model.getDuration().toString());
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
    public Set<Genre> getGenresByMovieId(UUID movieId) {
        try (Connection connection = JdbcConfiguration.getConnection();
             PreparedStatement preparedStatement
                     = connection.prepareStatement(
                     SQL_SELECT_GENRES_BY_MOVIE_ID)) {
            preparedStatement.setObject(1, movieId);
            Set<Genre> genres = new HashSet<>();
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    genres.add(genreMapper.mapJdbcResultToModel(rs));
                }
            }
            return genres;
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

    private void checkTitleAndProducerNameForUniqueness(Movie model) {
        try (Connection connection = JdbcConfiguration.getConnection();
             PreparedStatement preparedStatement
                     = connection.prepareStatement(SQL_SELECT_BY_TITLE_AND_PRODUCER_NAME)) {
            preparedStatement.setObject(1, model.getTitle());
            preparedStatement.setObject(2, model.getProducerName());
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    throw new UniqueFieldException(model.getClass().getName(),
                            model.getId(), "tile and producerName");
                }
            }
        }catch (SQLException e) {
            throw  new DataException(e.getMessage(),e.getCause());
        }
    }
}
