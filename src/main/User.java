package main;


public abstract class User {
    private String login;
    private String password;
    private String name;
    private UserStatus status;
    private String email;
    protected  UserType type;

    //Construdores
    public User(String login, String password, String name, String email, UserType type) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.email = email;
        this.type = type;
        this.status = UserStatus.INACTIVE;

    }
    // Getters e Setters
    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    //Metodos

    protected  enum UserStatus {
        ACTIVE,
        INACTIVE
    }
    
    protected  enum UserType {
        ADMIN,
        TECHNICIAN,
        CLIENT
    }
}

