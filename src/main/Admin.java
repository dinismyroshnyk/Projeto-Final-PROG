package main;

public class Admin extends User {
    public Admin(String login, String password, String name, String email) {
        super(login, password, name, email, UserType.ADMIN);
    }


}
