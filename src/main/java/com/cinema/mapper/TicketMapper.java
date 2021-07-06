package com.cinema.mapper;

import com.cinema.model.Ticket;
import lombok.RequiredArgsConstructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class TicketMapper {
    private final UserMapper userMapper;
    private final MovieMapper movieMapper;

    public Ticket mapJdbcResultToModel(ResultSet resultSet) throws SQLException {
        return Ticket.builder()
                .id(resultSet.getObject("id",UUID.class))
                .user(userMapper.mapJdbcResultToModelAsPartOfTicket(resultSet))
                .movie(movieMapper.mapJdbcResultToModelAsPartOfTicket(resultSet))
                .date(resultSet.getObject("date", LocalDateTime.class))
                .price(resultSet.getDouble("price"))
                .build();
    }
}
