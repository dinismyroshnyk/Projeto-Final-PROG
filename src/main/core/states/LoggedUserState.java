package main.core.states;

import main.core.InterfaceManager;
import main.core.KeyboardManager;

import java.util.Map;

import org.jline.terminal.Size;

public class LoggedUserState extends State {
    private final String username;
    private InputField selectedField = InputField.OPTION1;
    private enum InputField { OPTION1, OPTION2, OPTION3, OPTION4, NONE }
    private ActionBox selectedActionBox = ActionBox.NONE;
    private enum ActionBox { NONE, RETURN }
    private String selectedTestOption = "";

    private InterfaceManager.LayoutParameters layoutParams;


    public LoggedUserState(InterfaceManager interfaceManager, String username) {
        super(interfaceManager);
        this.username = username;
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
            handleInput(keyboardManager);
        } finally {
            keyboardManager.resetKeyStates();
        }
    }
    private void handleNavigation(KeyboardManager keyboardManager) {
        if(keyboardManager.isKeyPressed(KeyboardManager.Keys.ARROW_DOWN)){
            navigateField(true);
        } else if (keyboardManager.isKeyPressed(KeyboardManager.Keys.ARROW_UP)){
            navigateField(false);
        }
    }

    private void navigateField(boolean isDownNavigation) {
        Map<InputField, InputField> downNavigation = Map.of(
            InputField.OPTION1, InputField.OPTION2,
            InputField.OPTION2, InputField.OPTION3,
            InputField.OPTION3, InputField.OPTION4,
            InputField.OPTION4, InputField.NONE,
            InputField.NONE, InputField.NONE
        );

        Map<InputField, InputField> upNavigation = Map.of(
            InputField.OPTION1, InputField.OPTION1,
            InputField.OPTION2, InputField.OPTION1,
            InputField.OPTION3, InputField.OPTION2,
            InputField.OPTION4, InputField.OPTION3,
            InputField.NONE, InputField.OPTION4
        );

        selectedField = isDownNavigation
            ? downNavigation.getOrDefault(selectedField, InputField.NONE)
            : upNavigation.getOrDefault(selectedField, InputField.OPTION1);

        if (selectedField == InputField.NONE) {
            selectedActionBox = isDownNavigation ? ActionBox.RETURN : ActionBox.NONE;
        } else if (selectedField == InputField.OPTION4 && !isDownNavigation) {
            selectedActionBox = ActionBox.NONE;
        }
    }


    private void handleEnterKey(KeyboardManager keyboardManager) {
        if (selectedField != InputField.NONE){
            hendleMenuOptionSelection(keyboardManager);
        } else if (selectedActionBox == ActionBox.RETURN){
            exit();
        }
    }

    private void hendleMenuOptionSelection(KeyboardManager keyboardManager) {
        if(keyboardManager.isKeyPressed(KeyboardManager.Keys.ENTER)){
            switch (selectedField) {
                case OPTION1:
                    selectedTestOption = "Selected test option 1";
                    break;
                case OPTION2:
                    selectedTestOption = "Selected test option 2";
                    break;
                case OPTION3:
                    selectedTestOption = "Selected test option 3";
                    break;
                case OPTION4:
                    selectedTestOption = "Selected test option 4";
                    break;
                case NONE: break;
            }
        }
    }

    private void handleInput(KeyboardManager keyboardManager){
        if(keyboardManager.isKeyPressed(KeyboardManager.Keys.ENTER)){
            handleEnterKey(keyboardManager);
        }
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
            String.format("Hello %s\n%s", username, selectedTestOption),
            false
        );

        // Input box
        String inputContent = String.format(
            "%sTest Option 1\n%sTest Option 2\n%sTest Option 3\n%sTest Option 4",
            selectedField == InputField.OPTION1 ? "* " : "  ",
            selectedField == InputField.OPTION2 ? "* " : "  ",
            selectedField == InputField.OPTION3 ? "* " : "  ",
            selectedField == InputField.OPTION4 ? "* " : "  "
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
            selectedActionBox == ActionBox.RETURN ? "* RETURN" : "RETURN"
        );
    }
}