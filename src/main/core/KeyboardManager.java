package main.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class KeyboardManager {
    private static KeyboardManager keyboardManager;
    public static final int ASCII_PRINTABLE_START = 32;
    public static final int ASCII_PRINTABLE_END = 126;

    public enum Keys {
        ARROW_UP,
        ARROW_DOWN,
        ARROW_LEFT,
        ARROW_RIGHT,
        ENTER,
        BACKSPACE,
        Q
    }

    private enum EscapeSequence {
        UP(new int[]{91, 65}, new int[] {79, 65}),
        DOWN(new int[]{91, 66}, new int[] {79, 66}),
        RIGHT(new int[]{91, 67}, new int[] {79, 67}),
        LEFT(new int[]{91, 68}, new int[] {79, 68});

        private int[] seq1;
        private int[] seq2;

        EscapeSequence(int[] seq1, int[] seq2) {
            this.seq1 = seq1;
            this.seq2 = seq2;
        }

        private int[] getSeq1() { return seq1; }
        private int[] getSeq2() { return seq2; }

        private static Keys getKeyBySequence(int[] seq){
            for (EscapeSequence escapeSequence : values()){
                if(arrayEquals(escapeSequence.getSeq1(), seq) || arrayEquals(escapeSequence.getSeq2(), seq)){
                    switch (escapeSequence){
                        case UP: return Keys.ARROW_UP;
                        case DOWN: return Keys.ARROW_DOWN;
                        case LEFT: return Keys.ARROW_LEFT;
                        case RIGHT: return Keys.ARROW_RIGHT;
                    }
                }
            }
            return null;
        }

        private static boolean arrayEquals(int[] arr1, int[] arr2) {
            if (arr1.length != arr2.length) {
                return false;
            }
            for (int i = 0; i < arr1.length; i++) {
                if (arr1[i] != arr2[i]) {
                    return false;
                }
            }
            return true;
        }
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
            case 27:
                handleEscapeSequence();
                break;
            case 10: case 13:
                handleEnter();
                break;
            case 8: case 127:
                handleBackspace();
                break;
            case 'q':
                handleQ();
                break;
            default:
                // Ignore other keys
        }
    }

    private void handleEscapeSequence() {
        try {
            int nextKey1 = InterfaceManager.getInstance().getTerminal().reader().read();
            int nextKey2 = InterfaceManager.getInstance().getTerminal().reader().read();
            Keys key = EscapeSequence.getKeyBySequence(new int[]{nextKey1, nextKey2});
            if(key != null){
                keyStates.put(key, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleEnter() { keyStates.put(Keys.ENTER, true); }
    private void handleBackspace() { keyStates.put(Keys.BACKSPACE, true); }
    private void handleQ() { keyStates.put(Keys.Q, true); }

    public void resetKeyStates() {
        for (Keys key : Keys.values()) {
            keyStates.put(key, false);
        }
    }

    public boolean isKeyPressed(Keys key) { return keyStates.get(key); }
}