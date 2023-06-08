package ua.gaponov.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.Properties;

import static ua.gaponov.config.Constants.DEFAULT_FILE_NAME;

/**
 * @author Andriy Gaponov
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Config {

    public static Properties loadProperties() {
        Properties properties = new Properties();
        try {
            properties.load(Config.class.getClassLoader().getResourceAsStream(DEFAULT_FILE_NAME));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }
}
