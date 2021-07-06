package com.cinema.repository.user.impl;

import com.cinema.configuration.JdbcConfiguration;
import com.cinema.exception.DataException;
import com.cinema.exception.NotFoundException;
import com.cinema.exception.UniqueFieldException;
import com.cinema.mapper.UserMapper;
import com.cinema.model.Genre;
import com.cinema.model.User;
import com.cinema.repository.user.UserRepository;
import com.cinema.utility.validator.ValidatorUtility;
import java.io.IOException;
import java.rmi.UnexpectedException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserJdbcRepositoryImpl implements UserRepository {
    private final UserMapper userMapper;

    private final String SQL_SELECT_ALL = "SELECT * FROM public.users";

    private final String SQL_SELECT_BY_ID
            = "SELECT * "
            + "FROM public.users "
            + "WHERE id = ?";

    private final String SQL_INSERT
            = "INSERT INTO public.users (id, name, phone) VALUES (?, ? , ?)";

    private final String SQL_UPDATE
            = "UPDATE public.users "
            + "SET name = ?, phone = ? "
            + "WHERE id = ?";

    private final String SQL_DELETE_BY_ID = "DELETE FROM public.users WHERE id = ?";

    private final String SQL_SELECT_BY_PHONE
            = "SELECT * "
            + "FROM public.users "
            + "WHERE phone = ?";

    public UserJdbcRepositoryImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public List<User> getAll() {
        try (Connection connection = JdbcConfiguration.getConnection();
             PreparedStatement preparedStatement
                     = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = preparedStatement.executeQuery()) {
            List<User> models = new ArrayList<>();
            while (rs.next()) {
                models.add(userMapper.mapJdbcResultToModel(rs));
            }
            return models;
        } catch (SQLException e) {
            throw new DataException(e.getMessage(),e.getCause());
        }
    }

    @Override
    public User getById(UUID uuid) {
        try (Connection connection = JdbcConfiguration.getConnection();
             PreparedStatement preparedStatement
                     = connection.prepareStatement(SQL_SELECT_BY_ID)) {
            preparedStatement.setObject(1, uuid);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                checkIdForExisting(rs.next(), uuid);
                return userMapper.mapJdbcResultToModel(rs);
            }
        } catch (SQLException e) {
            throw  new DataException(e.getMessage(),e.getCause());
        }
    }

    @Override
    public void create(User model) {
        ValidatorUtility.validateModel(model);
        checkPhoneForUniqueness(model);
        try (Connection connection = JdbcConfiguration.getConnection();
             PreparedStatement preparedStatement
                     = connection.prepareStatement(SQL_INSERT)) {
            preparedStatement.setObject(1, model.getId());
            preparedStatement.setString(2, model.getName());
            preparedStatement.setString(3, model.getPhone());
            preparedStatement.executeUpdate();
        }catch (SQLException e) {
            throw  new DataException(e.getMessage(),e.getCause());
        }
    }

    @Override
    public void update(User model) {
        ValidatorUtility.validateModel(model);
        User oldModel = getById(model.getId());
        if(!oldModel.getPhone().equals(model.getPhone())){
            checkPhoneForUniqueness(model);
        }
        try (Connection connection = JdbcConfiguration.getConnection();
             PreparedStatement preparedStatement
                     = connection.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1, model.getName());
            preparedStatement.setString(2, model.getPhone());
            preparedStatement.setObject(3, model.getId());
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

    private void checkIdForExisting(boolean isRsNext, UUID id) {
        if (!isRsNext)
            throw new NotFoundException(Genre.class.getSimpleName(), id);
    }

    private void checkIdForExisting(int executeUpdateResult, UUID id) {
        if (executeUpdateResult == 0)
            throw new NotFoundException(Genre.class.getSimpleName(), id);
    }

    private void checkPhoneForUniqueness(User model) {
        try (Connection connection = JdbcConfiguration.getConnection();
             PreparedStatement preparedStatement
                     = connection.prepareStatement(SQL_SELECT_BY_PHONE)) {
            preparedStatement.setObject(1, model.getName());
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    throw new UniqueFieldException(model.getClass().getName(),
                            model.getId(), "phone");
                }
            }
        }catch (SQLException e) {
            throw  new DataException(e.getMessage(),e.getCause());
        }
    }
}
