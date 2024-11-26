package main;

public abstract class User {
    private String login;
    private String password;
    private String name;
    private UserStatus status;
    private String email;
    private UserType type;

    public enum UserStatus {
        ACTIVE,
        INACTIVE
    }

    public enum UserType {
        ADMIN,
        TECHNICIAN,
        CLIENT
    }
}