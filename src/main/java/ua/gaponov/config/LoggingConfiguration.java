package ua.gaponov.config;

import org.apache.log4j.*;

import java.util.Properties;

import static ua.gaponov.config.Constants.DEFAULT_FILE_NAME;

/**
 * @author Andriy Gaponov
 */
public final class LoggingConfiguration {
    public static final String LOG_PATTERN = "log4j.conversion.pattern";
    public static final String LOG_FILE = "log4j.appender.file";
    public static final String LOG_LEVEL = "log4j.logger.level";
    public static final String LOG_ENCODING = "log4j.logger.encoding";

    public void setup() {
        Properties properties = new Properties();
        try {
            properties.load(LoggingConfiguration.class.getClassLoader().getResourceAsStream(DEFAULT_FILE_NAME));

            // creates pattern layout
            PatternLayout layout = new PatternLayout();
            layout.setConversionPattern(properties.getProperty(LOG_PATTERN));

            // creates console appender
            ConsoleAppender consoleAppender = new ConsoleAppender();
            consoleAppender.setLayout(layout);
            consoleAppender.setEncoding(properties.getProperty(LOG_ENCODING));
            consoleAppender.activateOptions();

            // creates file appender
            DailyRollingFileAppender rollingFileAppender = new DailyRollingFileAppender();
            rollingFileAppender.setEncoding(properties.getProperty(LOG_ENCODING));
            rollingFileAppender.setFile(properties.getProperty(LOG_FILE));
            rollingFileAppender.setLayout(layout);
            rollingFileAppender.setDatePattern("'.'yyyy-MM-dd");
            rollingFileAppender.activateOptions();

            // configures the root logger
            Logger rootLogger = Logger.getRootLogger();
            rootLogger.setLevel(Level.toLevel(properties.getProperty(LOG_LEVEL)));
            rootLogger.removeAllAppenders();
            rootLogger.addAppender(consoleAppender);
            rootLogger.addAppender(rollingFileAppender);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
