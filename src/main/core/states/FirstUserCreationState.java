/*package main.core.states;

import main.core.InterfaceManager;
import main.core.KeyboardManager;
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
        KeyboardManager.getInstance().update(key);
        if (KeyboardManager.getInstance().isKeyPressed(KeyboardManager.Keys.Q)) {
            interfaceManager.clearScreen();
            System.exit(0);
        } else if (KeyboardManager.getInstance().isKeyPressed(KeyboardManager.Keys.ARROW_DOWN) || KeyboardManager.getInstance().isKeyPressed(KeyboardManager.Keys.ARROW_UP)) {
            isUsernameSelected = !isUsernameSelected;
        } else if (KeyboardManager.getInstance().isKeyPressed(KeyboardManager.Keys.BACKSPACE)) {
            if (isUsernameSelected && !username.isEmpty()) {
                username = username.substring(0, username.length() - 1);
            } else if (!isUsernameSelected && !password.isEmpty()) {
                password = password.substring(0, password.length() - 1);
            }
        } else if (key >= 32 && key <= 126) { // Printable ASCII range
            if (isUsernameSelected && username.length() < 10) {
                username += (char) key;
            } else if (!isUsernameSelected && password.length() < 10) {
                password += (char) key;
            }
        }
        KeyboardManager.getInstance().resetKeyStates();
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
}*/

package main.core.states;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import main.core.InterfaceManager;
import main.core.KeyboardManager;

import java.io.IOException;

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
    public void update(KeyStroke keyStroke) {
        KeyboardManager.getInstance().update(keyStroke);
        if (KeyboardManager.getInstance().isKeyPressed(KeyboardManager.Keys.Q)) {
            try {
                interfaceManager.clearScreen();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        } else if (KeyboardManager.getInstance().isKeyPressed(KeyboardManager.Keys.ARROW_DOWN) || KeyboardManager.getInstance().isKeyPressed(KeyboardManager.Keys.ARROW_UP) || KeyboardManager.getInstance().isKeyPressed(KeyboardManager.Keys.TAB)) {
            isUsernameSelected = !isUsernameSelected;
        } else if (KeyboardManager.getInstance().isKeyPressed(KeyboardManager.Keys.BACKSPACE)) {
            if (isUsernameSelected && !username.isEmpty()) {
                username = username.substring(0, username.length() - 1);
            } else if (!isUsernameSelected && !password.isEmpty()) {
                password = password.substring(0, password.length() - 1);
            }
        } else if (keyStroke != null && keyStroke.getKeyType() == com.googlecode.lanterna.input.KeyType.Character) { // Printable ASCII range
            char key = keyStroke.getCharacter();
            if (isUsernameSelected && username.length() < 10) {
                username += key;
            } else if (!isUsernameSelected && password.length() < 10) {
                password += key;
            }
        }
        KeyboardManager.getInstance().resetKeyStates();
    }

    @Override
    public void render(Screen screen, TerminalSize termSize, TerminalSize boxSize) throws IOException {
        int boxStartX = (termSize.getColumns() - boxSize.getColumns()) / 2;
        int boxStartY = (termSize.getRows() - boxSize.getRows()) / 2;
        TerminalSize outterBoxStartPos = new TerminalSize(boxStartX, boxStartY);

        // Draw outer box with instructions
        interfaceManager.drawOutterBox(screen, outterBoxStartPos, boxSize, "Press 'q' to quit, Tab to switch, Enter to confirm");

        // Message box
        TerminalSize messageBoxStartPos = new TerminalSize(outterBoxStartPos.getColumns() + 2, outterBoxStartPos.getRows() + 1);
        TerminalSize mainBoxSize = new TerminalSize(boxSize.getColumns() - 4, boxSize.getRows() - 20);
        interfaceManager.drawInnerBox(screen, messageBoxStartPos, mainBoxSize, "No admin user found !\nCreating admin...", false);

        // Input box
        TerminalSize inputBoxStartPos = new TerminalSize(outterBoxStartPos.getColumns() + 2, messageBoxStartPos.getRows() + mainBoxSize.getRows());
        String inputContent = String.format(
                "username: [%s%s]\npassword: [%s%s]",
                username,
                " ".repeat(10 - username.length()),
                "*".repeat(password.length()),
                " ".repeat(10 - password.length())
        );
        interfaceManager.drawInnerBox(screen, inputBoxStartPos, mainBoxSize, inputContent, true);

        // Action boxes
        TerminalSize quitBoxPos = new TerminalSize(outterBoxStartPos.getColumns() + 2, inputBoxStartPos.getRows() + mainBoxSize.getRows());
        TerminalSize confirmBoxPos = new TerminalSize(quitBoxPos.getColumns() + mainBoxSize.getColumns() / 2 + 2, quitBoxPos.getRows());
        TerminalSize actionBoxSize = new TerminalSize(mainBoxSize.getColumns() / 2 - 2, 3);
        interfaceManager.drawActionBox(screen, quitBoxPos, actionBoxSize, "QUIT");
        interfaceManager.drawActionBox(screen, confirmBoxPos, actionBoxSize, "CONFIRM");

        screen.refresh();
    }

    @Override
    public void exit() {
        super.exit();
    }
}