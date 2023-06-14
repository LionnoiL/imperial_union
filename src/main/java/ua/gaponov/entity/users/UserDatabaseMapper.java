package ua.gaponov.entity.users;

import lombok.extern.slf4j.Slf4j;
import ua.gaponov.database.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Andriy Gaponov
 */
@Slf4j
public class UserDatabaseMapper implements Mapper<User> {

    @Override
    public User map(ResultSet rs) {
        try {
            User user = new User();
            user.setId(rs.getLong("user_id"));
            user.setName(rs.getString("user_name"));
            user.setPassword(rs.getString("password"));
            user.setRole(UsersRole.valueOf(rs.getString("role")));

            return user;
        } catch (SQLException ex) {
            log.error("Error map user", ex);
        }
        return null;
    }
}
