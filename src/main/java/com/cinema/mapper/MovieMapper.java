package com.cinema.mapper;

import com.cinema.model.Movie;
import lombok.AllArgsConstructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.UUID;

@AllArgsConstructor
public class MovieMapper {
    public Movie mapJdbcResultToModel(ResultSet resultSet) throws SQLException {
        return Movie.builder()
                .id(resultSet.getObject("id", UUID.class))
                .title(resultSet.getString("title"))
                .producerName(resultSet.getString("producer_name"))
                .duration(Duration.parse(
                        resultSet.getString("duration")))
                .rating(resultSet.getDouble("rating"))
                .build();
    }

    public Movie mapJdbcResultToModelAsPartOfTicket(ResultSet resultSet) throws SQLException {
        return Movie.builder()
                .id(resultSet.getObject("movie_id", UUID.class))
                .title(resultSet.getString("movie_title"))
                .producerName(resultSet.getString("movie_producer_name"))
                .duration(Duration.parse(
                        resultSet.getString("movie_duration")))
                .rating(resultSet.getDouble("movie_rating"))
                .build();
    }
}
