package ua.gaponov.database;

import org.apache.commons.dbcp2.BasicDataSource;
import org.flywaydb.core.Flyway;
import ua.gaponov.config.Config;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import static ua.gaponov.config.Constants.*;

/**
 * @author Andriy Gaponov
 */
public class Database {

    private static BasicDataSource dataSource = null;
    private static final Properties properties = Config.loadProperties();

    static {
        dataSource = new BasicDataSource();
        dataSource.setUrl(properties.getProperty(CONNECTION_URL));
        dataSource.setUsername(properties.getProperty(CONNECTION_USERNAME));
        dataSource.setPassword(properties.getProperty(CONNECTION_PASSWORD));

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
