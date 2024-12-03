package main;

public class Client extends User{
    private CommonUserParams commonParams;

    //Construdores
    public Client(String login, String password, String name, String email) {
        super(login, password, name, email, UserType.CLIENT);
    }
}
