package com.cinema.configuration;

import com.cinema.exception.DataException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JdbcConfiguration {
    public static Connection getConnection(){
        Properties props = new Properties();
        String dbSettingsPropertyFile = "JdbcSettings.properties";
        try (FileReader fReader = new FileReader(dbSettingsPropertyFile)) {
            props.load(fReader);
            String url = props.getProperty("db.conn.url");
            String username = props.getProperty("db.username");
            String password = props.getProperty("db.password");
            return DriverManager.getConnection(url, username, password);
        } catch (IOException | SQLException e) {
            DataException exception
                    = new DataException(e.getMessage(), e.getCause());
            log.error("An exception occurred!", exception);
            throw exception;
        }
    }
}
