package main.core.states;

import main.core.InterfaceManager;
import org.jline.terminal.Size;
import java.util.Stack;

public abstract class State {
    protected InterfaceManager interfaceManager;
    private static Stack<State> stateStack = new Stack<>();

    public State(InterfaceManager interfaceManager) {
        this.interfaceManager = interfaceManager;
    }

    public void enter() {
        stateStack.push(this);
    }

    public void exit() {
        stateStack.pop();
        if (!stateStack.isEmpty()) {
            stateStack.peek().enter();
        }
    }

    public abstract void update(int key);
    public abstract void render(Size termSize, Size boxSize);

    public static State getCurrentState() {
        return stateStack.isEmpty() ? null : stateStack.peek();
    }

    public static Stack<State> getStateStack() {
        return stateStack;
    }
}