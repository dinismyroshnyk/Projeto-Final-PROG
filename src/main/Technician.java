package main;

public class Technician extends User {
    private CommonUserParams commonParams ;

    //Construdores
    public Technician(String login, String password, String name, String email) {
        super(login, password, name, email, UserType.TECHNICIAN);
    }
}
