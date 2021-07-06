package com.cinema.mapper;

import com.cinema.model.Genre;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class GenreMapper {
    public Genre mapJdbcResultToModel(ResultSet resultSet) throws SQLException {
        return Genre.builder()
                .id(UUID.fromString(resultSet.getString("id")))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .build();
    }
}
