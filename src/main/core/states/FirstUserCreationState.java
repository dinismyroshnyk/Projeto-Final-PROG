package main.core.states;

import main.core.InterfaceManager;
import org.jline.terminal.Terminal;
import org.jline.terminal.Size;

public class FirstUserCreationState extends State {
    private String username = "";
    private String password = "";
    private boolean isUsernameSelected = true;

    public FirstUserCreationState(InterfaceManager interfaceManager) {
        super(interfaceManager);
    }

    @Override
    public void enter() {
        super.enter();
    }

    @Override
    public void update(int key) {
        switch (key) {
            case 'q':
                // Quit application
                interfaceManager.clearScreen();
                System.exit(0);
                break;
            case '\t':
                // Toggle between username and password fields
                isUsernameSelected = !isUsernameSelected;
                break;
            case '\n':
                // Handle confirmation - could validate and transition to next state
                if (!username.isEmpty() && !password.isEmpty()) {
                    createAdminUser();
                }
                break;
            case 127: // Backspace
                if (isUsernameSelected && !username.isEmpty()) {
                    username = username.substring(0, username.length() - 1);
                } else if (!isUsernameSelected && !password.isEmpty()) {
                    password = password.substring(0, password.length() - 1);
                }
                break;
            default:
                // Add printable characters to username or password
                if (key >= 32 && key <= 126) { // Printable ASCII range
                    if (isUsernameSelected && username.length() < 10) {
                        username += (char) key;
                    } else if (!isUsernameSelected && password.length() < 10) {
                        password += (char) key;
                    }
                }
                break;
        }
    }

    @Override
    public void render(Terminal terminal, Size termSize, Size boxSize) {
        int boxStartX = (termSize.getColumns() - boxSize.getColumns()) / 2;
        int boxStartY = (termSize.getRows() - boxSize.getRows()) / 2;
        Size outterBoxStartPos = new Size(boxStartX, boxStartY);

        // Draw outer box with instructions
        interfaceManager.drawOutterBox(terminal, outterBoxStartPos, boxSize, "Press 'q' to quit, Tab to switch, Enter to confirm");

        // Message box
        Size messageBoxStartPos = new Size(outterBoxStartPos.getColumns() + 2, outterBoxStartPos.getRows() + 1);
        Size mainBoxSize = new Size(boxSize.getColumns() - 4, boxSize.getRows() - 20);
        interfaceManager.drawInnerBox(terminal, messageBoxStartPos, mainBoxSize, "No admin user found !\nCreating admin...", false);

        // Input box
        Size inputBoxStartPos = new Size(outterBoxStartPos.getColumns() + 2, messageBoxStartPos.getRows() + mainBoxSize.getRows());
        String inputContent = String.format(
            "username: [%s%s]\npassword: [%s%s]",
            username,
            " ".repeat(10 - username.length()),
            "*".repeat(password.length()),
            " ".repeat(10 - password.length())
        );
        interfaceManager.drawInnerBox(terminal, inputBoxStartPos, mainBoxSize, inputContent, true);

        // Action boxes
        Size quitBoxPos = new Size(outterBoxStartPos.getColumns() + 2, inputBoxStartPos.getRows() + mainBoxSize.getRows());
        Size confirmBoxPos = new Size(quitBoxPos.getColumns() + mainBoxSize.getColumns() / 2 + 2, quitBoxPos.getRows());
        Size actionBoxSize = new Size(mainBoxSize.getColumns() / 2 - 2, 3);
        interfaceManager.drawActionBox(terminal, quitBoxPos, actionBoxSize, "QUIT");
        interfaceManager.drawActionBox(terminal, confirmBoxPos, actionBoxSize, "CONFIRM");

        terminal.writer().flush();
    }

    @Override
    public void exit() {
        super.exit();
    }

    private void createAdminUser() {
        System.out.println("Creating admin user: " + username);
    }
}