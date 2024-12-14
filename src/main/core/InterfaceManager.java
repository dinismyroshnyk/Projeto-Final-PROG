/*package main.core;

import main.core.states.State;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal.Signal;

import java.io.IOException;

public class InterfaceManager {
    private static InterfaceManager interfaceManager;
    private Terminal terminal;
    private Size terminalSize;
    private Size boxSize;

    private InterfaceManager() {
        try {
            this.terminal = TerminalBuilder.builder().system(true).build();
            this.terminalSize = terminal.getSize();
            this.boxSize = new Size(60, 30);
            setupTerminalResizeHandler();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static InterfaceManager getInstance() {
        if (interfaceManager == null) {
            interfaceManager = new InterfaceManager();
        }
        return interfaceManager;
    }

    private void setupTerminalResizeHandler() {
        terminal.handle(Signal.WINCH, _ -> {
            Size newTermSize = terminal.getSize();
            terminalSize.copy(newTermSize);
            if (!State.getStateStack().isEmpty()) {
                State.getCurrentState().render(terminal, terminalSize, boxSize);
            }
        });
    }


    public void run() {
        try {
            terminal.enterRawMode();
            terminal.puts(InfoCmp.Capability.cursor_invisible);

            while (!State.getStateStack().isEmpty()) {
                State currentState = State.getCurrentState();
                clearScreen();
                currentState.render(terminal, terminalSize, boxSize);

                int key = readKey();
                currentState.update(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearScreen() {
        terminal.puts(InfoCmp.Capability.clear_screen);
        terminal.writer().flush();
    }

    private int readKey() throws IOException {
        return terminal.reader().read();
    }

    public void drawOutterBox(Terminal terminal, Size boxStartPos, Size boxSize, String instructions) {
        String horizontal = "─".repeat(boxSize.getColumns() - 2);

        // Top border
        terminal.writer().println("\033[" + boxStartPos.getRows() + ";" + boxStartPos.getColumns() + "H┌" + horizontal + "┐");

        // Vertical borders
        for (int i = 1; i < boxSize.getRows() - 1; i++) {
            terminal.writer().println("\033[" + (boxStartPos.getRows() + i) + ";" + boxStartPos.getColumns() + "H│" + " ".repeat(boxSize.getColumns() - 2) + "│");
        }

        // Dividing line
        int dividerY = boxStartPos.getRows() + boxSize.getRows() - 3;
        String dividerLine = "─".repeat(boxSize.getColumns() - 2);
        terminal.writer().println("\033[" + dividerY + ";" + boxStartPos.getColumns() + "H├" + dividerLine + "┤");

        // Instructions
        terminal.writer().println("\033[" + (dividerY + 1) + ";" + (boxStartPos.getColumns() + 2) + "H" + instructions);

        // Bottom border
        terminal.writer().println("\033[" + (boxStartPos.getRows() + boxSize.getRows() - 1) + ";" + boxStartPos.getColumns() + "H└" + horizontal + "┘");

        terminal.writer().flush();
    }

    public void drawInnerBox(Terminal terminal, Size boxStartPos, Size boxSize, String content, boolean isInputBox) {
        String horizontal = "─".repeat(boxSize.getColumns() - 2);

        // Top border
        terminal.writer().println("\033[" + boxStartPos.getRows() + ";" + boxStartPos.getColumns() + "H┌" + horizontal + "┐");

        // Vertical borders with content
        String[] lines = content.split("\n");
        for (int i = 1; i < boxSize.getRows() - 1; i++) {
            String line = (i - 1 < lines.length) ? lines[i - 1] : "";
            String paddedLine = line.length() > boxSize.getColumns() - 2
                ? line.substring(0, boxSize.getColumns() - 2)
                : line + " ".repeat(boxSize.getColumns() - 2 - line.length());

            // If it's an input box, add background for input areas
            if (isInputBox) {
                paddedLine = paddedLine.replace("[          ]", "\033[47m[          ]\033[0m");
            }

            terminal.writer().println("\033[" + (boxStartPos.getRows() + i) + ";" + boxStartPos.getColumns() + "H│" + paddedLine + "│");
        }

        // Bottom border
        terminal.writer().println("\033[" + (boxStartPos.getRows() + boxSize.getRows() - 1) + ";" + boxStartPos.getColumns() + "H└" + horizontal + "┘");

        terminal.writer().flush();
    }

    public void drawActionBox(Terminal terminal, Size boxStartPos, Size boxSize, String content) {
        String horizontal = "─".repeat(boxSize.getColumns() - 2);

        // Top border
        terminal.writer().println("\033[" + boxStartPos.getRows() + ";" + boxStartPos.getColumns() + "H┌" + horizontal + "┐");

        // Vertical borders with centered content
        int contentStart = (boxSize.getColumns() - 2 - content.length()) / 2;
        String emptyLine = " ".repeat(boxSize.getColumns() - 2);

        for (int i = 1; i < boxSize.getRows() - 1; i++) {
            String line = emptyLine;
            if (i == boxSize.getRows() / 2) {
                line = " ".repeat(contentStart) + content +" ".repeat(boxSize.getColumns() - 2 - contentStart - content.length());
            }
            terminal.writer().println("\033[" + (boxStartPos.getRows() + i) + ";" + boxStartPos.getColumns() + "H│" + line + "│");
        }

        // Bottom border
        terminal.writer().println("\033[" + (boxStartPos.getRows() + boxSize.getRows() - 1) + ";" + boxStartPos.getColumns() + "H└" + horizontal + "┘");

        terminal.writer().flush();
    }

    public Terminal getTerminal() { return terminal; }
    public Size getTerminalSize() { return terminalSize; }
    public Size getBoxSize() { return boxSize; }
}*/

package main.core;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import main.core.states.State;

import java.io.IOException;

public class InterfaceManager {
    private static InterfaceManager interfaceManager;
    private Terminal terminal;
    private Screen screen;
    private TerminalSize terminalSize;
    private TerminalSize boxSize;

    private InterfaceManager() {
        try {
            DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                defaultTerminalFactory.setForceTextTerminal(true); // Avoid stty issue
            }
            terminal = defaultTerminalFactory.createTerminal();
            screen = new TerminalScreen(terminal);

            screen.startScreen();
            this.terminalSize = terminal.getTerminalSize();
            this.boxSize = new TerminalSize(60, 30);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static InterfaceManager getInstance() {
        if (interfaceManager == null) {
            interfaceManager = new InterfaceManager();
        }
        return interfaceManager;
    }

    public void run() {
        try {
            while (!State.getStateStack().isEmpty()) {
                State currentState = State.getCurrentState();
                clearScreen();
                currentState.render(screen, terminalSize, boxSize);

                KeyStroke key = readKey();
                currentState.update(key);
            }
            screen.stopScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearScreen() throws IOException {
        screen.clear();
        screen.refresh();
    }

    private KeyStroke readKey() throws IOException {
        return screen.readInput();
    }

    public void drawOutterBox(Screen screen, TerminalSize boxStartPos, TerminalSize boxSize, String instructions) {
        TextGraphics graphics = screen.newTextGraphics();

        String horizontal = "─".repeat(boxSize.getColumns() - 2);

        // Top border
        graphics.putString(boxStartPos.getColumns(), boxStartPos.getRows(), "┌" + horizontal + "┐");

        // Vertical borders
        for (int i = 1; i < boxSize.getRows() - 1; i++) {
            graphics.putString(boxStartPos.getColumns(), boxStartPos.getRows() + i, "│" + " ".repeat(boxSize.getColumns() - 2) + "│");
        }

        // Dividing line
        int dividerY = boxStartPos.getRows() + boxSize.getRows() - 3;
        String dividerLine = "─".repeat(boxSize.getColumns() - 2);
        graphics.putString(boxStartPos.getColumns(), dividerY, "├" + dividerLine + "┤");

        // Instructions
        graphics.putString(boxStartPos.getColumns() + 2, dividerY + 1, instructions);

        // Bottom border
        graphics.putString(boxStartPos.getColumns(), boxStartPos.getRows() + boxSize.getRows() - 1, "└" + horizontal + "┘");

        try {
            screen.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void drawInnerBox(Screen screen, TerminalSize boxStartPos, TerminalSize boxSize, String content, boolean isInputBox) {
        TextGraphics graphics = screen.newTextGraphics();
        String horizontal = "─".repeat(boxSize.getColumns() - 2);

        // Top border
        graphics.putString(boxStartPos.getColumns(), boxStartPos.getRows(), "┌" + horizontal + "┐");

        // Vertical borders with content
        String[] lines = content.split("\n");
        for (int i = 1; i < boxSize.getRows() - 1; i++) {
            String line = (i - 1 < lines.length) ? lines[i - 1] : "";
            String paddedLine = line.length() > boxSize.getColumns() - 2
                    ? line.substring(0, boxSize.getColumns() - 2)
                    : line + " ".repeat(boxSize.getColumns() - 2 - line.length());

            // If it's an input box, add background for input areas
            if (isInputBox) {
                int index = paddedLine.indexOf("[          ]");
                if(index != -1) {
                    graphics.setBackgroundColor(TextColor.ANSI.WHITE);
                    graphics.putString(boxStartPos.getColumns() + 1 + index, boxStartPos.getRows() + i, "[          ]");
                    graphics.setBackgroundColor(TextColor.ANSI.DEFAULT);
                    paddedLine = paddedLine.replace("[          ]", "[          ]"); // To keep original text
                }
            }

            graphics.putString(boxStartPos.getColumns(), boxStartPos.getRows() + i, "│" + paddedLine + "│");

        }

        // Bottom border
        graphics.putString(boxStartPos.getColumns(), boxStartPos.getRows() + boxSize.getRows() - 1, "└" + horizontal + "┘");
        try {
            screen.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void drawActionBox(Screen screen, TerminalSize boxStartPos, TerminalSize boxSize, String content) {
        TextGraphics graphics = screen.newTextGraphics();
        String horizontal = "─".repeat(boxSize.getColumns() - 2);

        // Top border
        graphics.putString(boxStartPos.getColumns(), boxStartPos.getRows(), "┌" + horizontal + "┐");

        // Vertical borders with centered content
        int contentStart = (boxSize.getColumns() - 2 - content.length()) / 2;
        String emptyLine = " ".repeat(boxSize.getColumns() - 2);

        for (int i = 1; i < boxSize.getRows() - 1; i++) {
            String line = emptyLine;
            if (i == boxSize.getRows() / 2) {
                line = " ".repeat(contentStart) + content + " ".repeat(boxSize.getColumns() - 2 - contentStart - content.length());
            }
            graphics.putString(boxStartPos.getColumns(), boxStartPos.getRows() + i, "│" + line + "│");
        }

        // Bottom border
        graphics.putString(boxStartPos.getColumns(), boxStartPos.getRows() + boxSize.getRows() - 1, "└" + horizontal + "┘");
        try {
            screen.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public TerminalSize getTerminalSize() {
        return terminalSize;
    }

    public TerminalSize getBoxSize() {
        return boxSize;
    }
}