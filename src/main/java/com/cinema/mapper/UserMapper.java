package com.cinema.mapper;

import com.cinema.model.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserMapper {
    public User mapJdbcResultToModel(ResultSet resultSet) throws SQLException {
        return User.builder()
                .id(resultSet.getObject("id", UUID.class))
                .name(resultSet.getString("name"))
                .phone(resultSet.getString("phone"))
                .build();
    }

    public User mapJdbcResultToModelAsPartOfTicket(ResultSet resultSet) throws SQLException {
        return User.builder()
                .id(resultSet.getObject("user_id", UUID.class))
                .name(resultSet.getString("user_name"))
                .phone(resultSet.getString("user_phone"))
                .build();
    }
}
