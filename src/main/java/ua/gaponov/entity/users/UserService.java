package ua.gaponov.entity.users;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import ua.gaponov.database.SqlHelper;
import ua.gaponov.database.StatementParameters;

import java.util.Objects;

/**
 * @author Andriy Gaponov
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class UserService {

    private static final UserDatabaseMapper MAPPER = new UserDatabaseMapper();
    private static final SqlHelper<User> USER_SQL_HELPER = new SqlHelper<>();
    private static final String SALT = "$2a$12$6MScoIJF1i38dSZXePsdqe";

    public static String hashPassword(String passwordPlaintext) {
        String hashedPassword = BCrypt.hashpw(passwordPlaintext, SALT);
        return (hashedPassword);
    }

    public static boolean login(String userName, String userPassword) {
        User user = getByName(userName);
        if (user == null) {
            return false;
        }

        if (Objects.equals(user.getPassword(), hashPassword(userPassword))) {
            return true;
        }
        return false;
    }

    public static User getByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        StatementParameters<String> parameters = StatementParameters.build(name);
        String sql = """
                SELECT * FROM users
                WHERE users.user_name = ?
                """;
        return USER_SQL_HELPER.getOne(sql, parameters, MAPPER);
    }
}
