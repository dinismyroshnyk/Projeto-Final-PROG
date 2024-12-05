package main;

import main.core.InterfaceManager;
import main.core.states.FirstUserCreationState;

public class App {
    public static void main(String[] args) {
        InterfaceManager interfaceManager = new InterfaceManager();

        if (!adminUserExists()) {
            FirstUserCreationState firstUserCreationState = new FirstUserCreationState(interfaceManager);
            firstUserCreationState.enter(); // Call enter() method to start the state
        } else {
            // Go to login state or main menu state (add this logic later)
        }

        interfaceManager.run();
    }

    private static boolean adminUserExists() {
        return false; // Default to false for first-time setup
    }
}