package main.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class KeyboardManager {
    private static KeyboardManager keyboardManager;
    public enum Keys {
        ARROW_UP,
        ARROW_DOWN,
        ARROW_LEFT,
        ARROW_RIGHT,
        ENTER,
        BACKSPACE,
        TAB,
        Q
    }

    private Map<Keys, Boolean> keyStates = new HashMap<>();

    private KeyboardManager() {
        for (Keys key : Keys.values()) {
            keyStates.put(key, false);
        }
    }

    public static KeyboardManager getInstance() {
        if (keyboardManager == null) {
            keyboardManager = new KeyboardManager();
        }
        return keyboardManager;
    }

    public void update(int key) {
        switch (key) {
            case 27: // Escape sequence start
                // Handle escape sequences (arrow keys, etc.)
                try {
                    int nextKey1 = InterfaceManager.getInstance().getTerminal().reader().read();
                    int nextKey2 = InterfaceManager.getInstance().getTerminal().reader().read();
                    if(nextKey1 == 91){ //ESC [
                        switch(nextKey2){
                            case 65: keyStates.put(Keys.ARROW_UP, true); break; //Up
                            case 66: keyStates.put(Keys.ARROW_DOWN, true); break; //Down
                            case 67: keyStates.put(Keys.ARROW_RIGHT, true); break; //Right
                            case 68: keyStates.put(Keys.ARROW_LEFT, true); break; //Left
                        }
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
                break;
            case 10: keyStates.put(Keys.ENTER, true); break; //Enter
            case 127: keyStates.put(Keys.BACKSPACE, true); break; //Backspace
            // case '\t': keyStates.put(Keys.TAB, true); break; //Tab
            case 'q': keyStates.put(Keys.Q, true); break; //q
            default:
                // Ignore other keys
        }
    }

    public void resetKeyStates() {
        for (Keys key : Keys.values()) {
            keyStates.put(key, false);
        }
    }


    public boolean isKeyPressed(Keys key) {
        return keyStates.get(key);
    }
}