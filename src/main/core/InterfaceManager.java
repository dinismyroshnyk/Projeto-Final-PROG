package main.core;

import main.core.states.State;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal.Signal;

import java.io.IOException;

public class InterfaceManager {
    private Terminal terminal;
    private Size terminalSize;
    private Size boxSize;

    public InterfaceManager() {
        try {
            this.terminal = TerminalBuilder.builder().system(true).build();
            this.terminalSize = terminal.getSize();
            this.boxSize = new Size(60, 30);
            setupTerminalResizeHandler();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

            while (!State.getStateStack().isEmpty()) {
                State currentState = State.getCurrentState();
                clearScreen();
                currentState.render(terminal, terminalSize, boxSize);

                int key = readKey();
                currentState.update(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                clearScreen();
                terminal.writer().flush();
                terminal.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
}