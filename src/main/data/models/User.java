package main.data.models;

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

    public User(String login, String password, String name, String email, UserType type) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.email = email;
        this.type = type;
        this.status = UserStatus.ACTIVE;
    }

    // Getters
    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public UserType getType() {
        return type;
    }

    public UserStatus getStatus() {
        return status;
    }

    // Setters
    public void setPassword(String password) {
        this.password = password;
    }

    //public void setStatus(UserStatus status) {
    //   this.status = status;
    //}
}