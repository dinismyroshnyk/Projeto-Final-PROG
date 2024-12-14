package main.core.states;

import main.core.InterfaceManager;
import main.core.KeyboardManager;
import org.jline.terminal.Size;

public class FirstUserCreationState extends State {

    private static final int MAX_INPUT_LENGTH = 10;
    private StringBuilder username = new StringBuilder();
    private StringBuilder password = new StringBuilder();
    private boolean isUsernameSelected = true;
    private InterfaceManager.LayoutParameters layoutParams;

    public FirstUserCreationState(InterfaceManager interfaceManager) {
        super(interfaceManager);
    }

    @Override
    public void enter() { super.enter(); }

    @Override
    public void exit() { super.exit(); }

    @Override
    public void update(int key) {
        KeyboardManager keyboardManager = KeyboardManager.getInstance();
        keyboardManager.update(key);

        try {
            handleSpecialKeys(keyboardManager, key);
            handleInputKeys(keyboardManager, key);
        } finally {
            keyboardManager.resetKeyStates();
        }
    }

    private void handleSpecialKeys(KeyboardManager keyboardManager, int key) {
        if (keyboardManager.isKeyPressed(KeyboardManager.Keys.Q)) {
            interfaceManager.clearScreen();
            System.exit(0);
        } else if (keyboardManager.isKeyPressed(KeyboardManager.Keys.ARROW_DOWN) || keyboardManager.isKeyPressed(KeyboardManager.Keys.ARROW_UP)) {
            isUsernameSelected = !isUsernameSelected;
        }
    }

    private void handleInputKeys(KeyboardManager keyboardManager, int key) {
        if (keyboardManager.isKeyPressed(KeyboardManager.Keys.BACKSPACE)) {
            handleBackspace();
        } else if (isValidPrintableChar(key)) {
            handleCharInput(key);
        }
    }

    private void handleBackspace() {
        if (isUsernameSelected && username.length() > 0) {
            username.deleteCharAt(username.length() - 1);
        } else if (!isUsernameSelected && password.length() > 0) {
            password.deleteCharAt(password.length() - 1);
        }
    }

    private void handleCharInput(int key) {
        if (isUsernameSelected && username.length() < MAX_INPUT_LENGTH) {
            username.append((char) key);
        } else if (!isUsernameSelected && password.length() < MAX_INPUT_LENGTH) {
            password.append((char) key);
        }
    }

    private boolean isValidPrintableChar(int key) {
        return key >= KeyboardManager.ASCII_PRINTABLE_START && key <= KeyboardManager.ASCII_PRINTABLE_END;
    }

    @Override
    public void render(Size termSize, Size boxSize) {
        layoutParams = new InterfaceManager.LayoutParameters(termSize, boxSize);

        // Draw outer box with instructions
        interfaceManager.drawOutterBox(
            layoutParams.getOutterBoxStartPos(),
            boxSize,
            "Press 'q' to quit, Tab to switch, Enter to confirm"
        );

        // Message box
        interfaceManager.drawInnerBox(
            layoutParams.getMessageBoxStartPos(),
            layoutParams.getMainBoxSize(),
            "No admin user found !\nCreating admin...",
            false
        );

        // Input box
        String inputContent = String.format(
            "username: [%s%s]\npassword: [%s%s]",
            username.toString(),
            " ".repeat(MAX_INPUT_LENGTH - username.length()),
            "*".repeat(password.length()),
            " ".repeat(MAX_INPUT_LENGTH - password.length())
        );

        interfaceManager.drawInnerBox(
            layoutParams.getInputBoxStartPos(),
            layoutParams.getMainBoxSize(),
            inputContent,
            true
        );

        // Action boxes
        interfaceManager.drawActionBox(
            layoutParams.getQuitBoxPos(),
            layoutParams.getActionBoxSize(),
            "QUIT"
        );

        interfaceManager.drawActionBox(
            layoutParams.getConfirmBoxPos(),
            layoutParams.getActionBoxSize(),
            "CONFIRM"
        );
    }
}