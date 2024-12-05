package main;

import java.io.IOException;

import org.jline.utils.InfoCmp;

import main.core.InterfaceManager;
import main.core.states.FirstUserCreationState;

public class App {
    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            InterfaceManager.getInstance().clearScreen();
            InterfaceManager.getInstance().getTerminal().puts(InfoCmp.Capability.cursor_visible);
            InterfaceManager.getInstance().getTerminal().writer().flush();
            try {
                InterfaceManager.getInstance().getTerminal().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        if (!adminUserExists()) {
            FirstUserCreationState firstUserCreationState = new FirstUserCreationState(InterfaceManager.getInstance());
            firstUserCreationState.enter();
        } else {
            // Go to login state or main menu state (add this logic later)
        }

        InterfaceManager.getInstance().run();
    }

    private static boolean adminUserExists() {
        return false; // Default to false for first-time setup
    }
}