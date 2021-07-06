package com.cinema.repository.genre.impl;

import com.cinema.configuration.JdbcConfiguration;
import com.cinema.exception.DataException;
import com.cinema.exception.NotFoundException;
import com.cinema.exception.UniqueFieldException;
import com.cinema.mapper.GenreMapper;
import com.cinema.model.Genre;
import com.cinema.repository.genre.GenreRepository;
import com.cinema.utility.validator.ValidatorUtility;
import java.io.IOException;
import java.rmi.UnexpectedException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GenreJdbcRepositoryImpl implements GenreRepository {
    private final GenreMapper genreMapper;

    private final String SQL_SELECT_ALL = "SELECT * FROM public.genre";

    private final String SQL_SELECT_BY_ID
            = "SELECT * "
            + "FROM public.genre "
            + "WHERE id = ?";

    private final String SQL_INSERT
            = "INSERT INTO public.genre (id, name, description) VALUES(?,?,?)";

    private final String SQL_UPDATE
            = "UPDATE public.genre "
            + "SET name = ?, description = ? "
            + "WHERE id = ?";

    private final String SQL_DELETE_BY_ID = "DELETE FROM public.genre WHERE id = ?";

    private final String SQL_SELECT_BY_NAME
            = "SELECT * "
            + "FROM public.genre "
            + "WHERE name = ?";

    public GenreJdbcRepositoryImpl(GenreMapper genreMapper) {
        this.genreMapper = genreMapper;
    }


    @Override
    public List<Genre> getAll() {
        List<Genre> models = new ArrayList<>();
        try (Connection connection = JdbcConfiguration.getConnection();
             PreparedStatement preparedStatement
                     = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {
                models.add(genreMapper.mapJdbcResultToModel(rs));
            }
            return models;

        } catch (SQLException e) {
            throw new DataException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public Genre getById(UUID uuid) {
        try (Connection connection = JdbcConfiguration.getConnection();
             PreparedStatement preparedStatement
                     = connection.prepareStatement(SQL_SELECT_BY_ID)) {

            preparedStatement.setObject(1, uuid);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                checkIdForExisting(rs.next(), uuid);
                return genreMapper.mapJdbcResultToModel(rs);
            }

        } catch (SQLException e) {
            throw new DataException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void create(Genre model) {
        ValidatorUtility.validateModel(model);
        checkNameForUniqueness(model);
        try (Connection connection = JdbcConfiguration.getConnection();
             PreparedStatement preparedStatement
                     = connection.prepareStatement(SQL_INSERT)) {

            preparedStatement.setObject(1, model.getId());
            preparedStatement.setString(2, model.getName());
            preparedStatement.setString(3, model.getDescription());
            preparedStatement.executeUpdate();

        }catch (SQLException e) {
            throw new DataException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void update(Genre model) {
        ValidatorUtility.validateModel(model);
        Genre oldModel = getById(model.getId());
        if(!oldModel.equals(model)){
            checkNameForUniqueness(model);
        }
        try (Connection connection = JdbcConfiguration.getConnection();
             PreparedStatement preparedStatement
                     = connection.prepareStatement(SQL_UPDATE)) {

            preparedStatement.setString(1, model.getName());
            preparedStatement.setString(2, model.getDescription());
            preparedStatement.setObject(3, model.getId());
            preparedStatement.executeUpdate();

        }catch (SQLException e) {
            throw new DataException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void deleteById(UUID uuid) {
        try (Connection connection = JdbcConfiguration.getConnection();
             PreparedStatement preparedStatement
                     = connection.prepareStatement(SQL_DELETE_BY_ID)) {

            preparedStatement.setObject(1,uuid);
            checkIdForExisting(preparedStatement.executeUpdate(), uuid);

        }catch (SQLException e) {
            throw new DataException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void synchronize() throws IOException {
        throw new UnexpectedException("Method is not defined");
    }

    private void checkIdForExisting(boolean isRsNext, UUID id) {
        if (!isRsNext){
            throw new NotFoundException(Genre.class.getSimpleName(), id);
        }
    }

    private void checkIdForExisting(int executeUpdateResult, UUID id) {
        if (executeUpdateResult == 0){
            throw new NotFoundException(Genre.class.getSimpleName(), id);
        }
    }

    private void checkNameForUniqueness(Genre model) {
        try (Connection connection = JdbcConfiguration.getConnection();
             PreparedStatement preparedStatement
                     = connection.prepareStatement(SQL_SELECT_BY_NAME)) {
            preparedStatement.setObject(1, model.getName());
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    throw new UniqueFieldException(model.getClass().getName(),
                                               model.getId(), "name");
                }
            }
        }catch (SQLException e) {
            throw new DataException(e.getMessage(),e.getCause());
        }
    }
}
