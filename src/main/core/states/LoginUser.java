import main.data.models.User;

public class LoginUser {
    private UserRepository userRepository = new UserRepository();

    public void register(User user) {
        if (userRepository.findByLogin(user.getLogin()) == null) {
            userRepository.save(user);
            System.out.println("Usuário registrado com sucesso!");
        } else {
            System.out.println("Usuário já existe!");
        }
    }

    public boolean authenticate(String login, String password) {
        User user = userRepository.findByLogin(login);
        if (user != null && user.getPassword().equals(password)) {
            System.out.println("Login bem-sucedido!");
            return true;
        }
        System.out.println("Credenciais inválidas.");
        return false;
    }
}
