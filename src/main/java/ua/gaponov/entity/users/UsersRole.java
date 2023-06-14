package ua.gaponov.entity.users;

/**
 * @author Andriy Gaponov
 */
public enum UsersRole {
    ROLE_ADMIN ("Admin"),
    ROLE_USER ("User");

    private String friendlyName;

    UsersRole(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName() {
        return friendlyName;
    }
}
