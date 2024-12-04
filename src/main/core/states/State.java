package main.core.states;

import main.core.InterfaceManager;
import org.jline.terminal.Terminal;
import org.jline.terminal.Size;

public abstract class State {
    protected InterfaceManager interfaceManager;

    public State(InterfaceManager interfaceManager) {
        this.interfaceManager = interfaceManager;
    }

    public abstract void enter();
    public abstract void update(int key);
    public abstract void render(Terminal terminal, Size termSize, Size boxSize);
    public abstract void exit();
}