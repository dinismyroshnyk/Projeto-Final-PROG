package main.core.states;

import main.core.InterfaceManager;
import main.core.KeyboardManager;

import java.util.Map;

import org.jline.terminal.Size;

public class FirstUserCreationState extends State {

    private static final int MAX_INPUT_LENGTH = 10;
    private StringBuilder username = new StringBuilder();
    private StringBuilder password = new StringBuilder();
    private InputField selectedField = InputField.USERNAME;
    private ActionBox selectedActionBox = ActionBox.NONE;
    private enum InputField { NONE, USERNAME, PASSWORD }
    private enum ActionBox { NONE, QUIT, CONFIRM }
    private InterfaceManager.LayoutParameters layoutParams;

    public FirstUserCreationState(InterfaceManager interfaceManager) {
        super(interfaceManager);
    }

    @Override
    public void enter() {
        super.enter();
    }

    @Override
    public void exit() {
        super.exit();
    }

    @Override
    public void update(int key) {
        KeyboardManager keyboardManager = KeyboardManager.getInstance();
        keyboardManager.update(key);

        try {
            handleNavigation(keyboardManager);
            handleInput(keyboardManager, key);
        } finally {
            keyboardManager.resetKeyStates();
        }
    }

    private void handleNavigation(KeyboardManager keyboardManager) {
        Map<InputField, InputField> fieldDownNavigation = Map.of(
            InputField.USERNAME, InputField.PASSWORD,
            InputField.PASSWORD, InputField.NONE
        );

        Map<InputField, InputField> fieldUpNavigation = Map.of(
            InputField.PASSWORD, InputField.USERNAME
        );

        Map<ActionBox, ActionBox> actionBoxLeftNavigation = Map.of(
            ActionBox.CONFIRM, ActionBox.QUIT
        );

        Map<ActionBox, ActionBox> actionBoxRightNavigation = Map.of(
            ActionBox.QUIT, ActionBox.CONFIRM
        );

        if (selectedActionBox == ActionBox.NONE) {
            handleInputFieldNavigation(keyboardManager, fieldDownNavigation, fieldUpNavigation);
        } else {
            handleActionBoxNavigation(keyboardManager, actionBoxLeftNavigation, actionBoxRightNavigation);
        }
    }

    private void handleInputFieldNavigation(KeyboardManager keyboardManager, Map<InputField, InputField> fieldDownNavigation, Map<InputField, InputField> fieldUpNavigation) {
        if (keyboardManager.isKeyPressed(KeyboardManager.Keys.ARROW_DOWN)) {
            selectedField = fieldDownNavigation.getOrDefault(selectedField, InputField.NONE);
            if (selectedField == InputField.NONE) {
                selectedActionBox = ActionBox.QUIT;
            }
        } else if (keyboardManager.isKeyPressed(KeyboardManager.Keys.ARROW_UP)) {
            selectedField = fieldUpNavigation.getOrDefault(selectedField, selectedField);
        }
    }

    private void handleActionBoxNavigation(KeyboardManager keyboardManager, Map<ActionBox, ActionBox> actionBoxLeftNavigation, Map<ActionBox, ActionBox> actionBoxRightNavigation) {
        if (keyboardManager.isKeyPressed(KeyboardManager.Keys.ARROW_LEFT)) {
            selectedActionBox = actionBoxLeftNavigation.getOrDefault(selectedActionBox, selectedActionBox);
        } else if (keyboardManager.isKeyPressed(KeyboardManager.Keys.ARROW_RIGHT)) {
            selectedActionBox = actionBoxRightNavigation.getOrDefault(selectedActionBox, selectedActionBox);
        } else if (keyboardManager.isKeyPressed(KeyboardManager.Keys.ARROW_UP)) {
            selectedActionBox = ActionBox.NONE;
            selectedField = InputField.PASSWORD;
        }
    }

    private void handleEnterKey(){
        if(selectedActionBox == ActionBox.QUIT){
            interfaceManager.clearScreen();
            System.exit(0);
        } else if (selectedActionBox == ActionBox.CONFIRM){
            new LoggedUserState(InterfaceManager.getInstance(), username.toString()).enter();
        }
    }

    private void handleInput(KeyboardManager keyboardManager, int key) {
        if (keyboardManager.isKeyPressed(KeyboardManager.Keys.BACKSPACE)) {
            handleBackspace();
        } else if (keyboardManager.isKeyPressed(KeyboardManager.Keys.ENTER)) {
            handleEnterKey();
        } else if (isValidPrintableChar(key)) {
            handleCharInput(key);
        }
    }

    private void handleBackspace() {
        if (selectedField == InputField.USERNAME && username.length() > 0) {
            username.deleteCharAt(username.length() - 1);
        } else if (selectedField == InputField.PASSWORD && password.length() > 0) {
            password.deleteCharAt(password.length() - 1);
        }
    }

    private void handleCharInput(int key) {
        if (selectedField == InputField.USERNAME && username.length() < MAX_INPUT_LENGTH) {
            username.append((char) key);
        } else if (selectedField == InputField.PASSWORD && password.length() < MAX_INPUT_LENGTH) {
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
            "Navigate with ↑ ↓ → ←, Enter to select"
        );

        // Message box
        interfaceManager.drawInnerBox(
            layoutParams.getMessageBoxStartPos(),
            layoutParams.getMainBoxSize(),
            "No admin user found !\nCreating admin...",
            false
        );

        // Input box
        String usernameFormatted = String.format("%s%s", username.toString(), " ".repeat(MAX_INPUT_LENGTH - username.length()));
        String passwordFormatted = String.format("%s%s", "*".repeat(password.length()), " ".repeat(MAX_INPUT_LENGTH - password.length()));

        String inputContent = String.format(
            "%susername: %s\n%spassword: %s",
            selectedField == InputField.USERNAME ? "* " : "  ", usernameFormatted,
            selectedField == InputField.PASSWORD ? "* " : "  ",  passwordFormatted
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
            selectedActionBox == ActionBox.QUIT ? "* QUIT" : "QUIT"
        );

        interfaceManager.drawActionBox(
            layoutParams.getConfirmBoxPos(),
            layoutParams.getActionBoxSize(),
            selectedActionBox == ActionBox.CONFIRM ? "* CONFIRM" : "CONFIRM"
        );
    }
}