package ua.gaponov.database;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import org.flywaydb.core.Flyway;
import ua.gaponov.config.Config;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

import static ua.gaponov.config.Constants.*;

/**
 * @author Andriy Gaponov
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class Database {

    private static BasicDataSource dataSource;
    private static final Properties properties = Config.loadProperties();

    static {
        dataSource = new BasicDataSource();

        if (Objects.nonNull(System.getenv("url"))){
            dataSource.setUrl(System.getenv("url"));
        } else {
            dataSource.setUrl(properties.getProperty(CONNECTION_URL_MYSQL));
        }

        if (Objects.nonNull(System.getenv("username"))){
            dataSource.setUsername(System.getenv("username"));
        } else {
            dataSource.setUsername(properties.getProperty(CONNECTION_USERNAME_MYSQL));
        }

        if (Objects.nonNull(System.getenv("password"))){
            dataSource.setPassword(System.getenv("password"));
        } else {
            dataSource.setPassword(properties.getProperty(CONNECTION_PASSWORD_MYSQL));
        }

        dataSource.setMinIdle(5);
        dataSource.setMaxIdle(10);
        dataSource.setMaxTotal(25);
        dataSource.setMaxOpenPreparedStatements(100);

        migrate();
    }

    public static void setDataSource(BasicDataSource dataSource) {
        Database.dataSource = dataSource;
    }

    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void migrate() {
        Flyway flyway = Flyway
                .configure()
                .encoding(StandardCharsets.UTF_8)
                .loggers("slf4j")
                .dataSource(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword())
                .failOnMissingLocations(true)
                .load();
        flyway.migrate();
    }

    public static void execSql(String sql) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            //NOP
        }
    }
}
