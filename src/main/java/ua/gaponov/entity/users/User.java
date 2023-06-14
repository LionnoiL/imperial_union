package ua.gaponov.entity.users;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Andriy Gaponov
 */
@Getter
@Setter
public class User {

    private long id;
    private String name;
    private String password;
    private UsersRole role;
}
