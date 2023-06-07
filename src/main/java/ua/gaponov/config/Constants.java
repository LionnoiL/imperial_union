package ua.gaponov.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Andriy Gaponov
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    public static final String CONNECTION_URL_MYSQL = "com.mysql.url";
    public static final String CONNECTION_USERNAME_MYSQL = "com.mysql.username";
    public static final String CONNECTION_PASSWORD_MYSQL = "com.mysql.password";
    public static final String CONNECTION_URL_H2 = "com.h2.url";
    public static final String CONNECTION_USERNAME_H2 = "com.h2.username";
    public static final String CONNECTION_PASSWORD_H2 = "com.h2.password";
}
