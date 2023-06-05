package ua.gaponov.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ua.gaponov.utils.FilesUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Andriy Gaponov
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Config {

    public static Properties loadProperties() {
        Properties properties = new Properties();
        try {
            properties.load(FilesUtils.getFileInputStream("config/application.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }
}
