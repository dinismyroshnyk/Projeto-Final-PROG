package main.core;

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
    private StringBuilder renderBuffer;

    public static class LayoutParameters {
        private Size outterBoxStartPos;
        private Size messageBoxStartPos;
        private Size mainBoxSize;
        private Size inputBoxStartPos;
        private Size quitBoxPos;
        private Size confirmBoxPos;
        private Size actionBoxSize;

        public LayoutParameters(Size terminalSize, Size boxSize) {
            int boxStartX = (terminalSize.getColumns() - boxSize.getColumns()) / 2;
            int boxStartY = (terminalSize.getRows() - boxSize.getRows()) / 2;

            this.outterBoxStartPos = new Size(boxStartX, boxStartY);
            this.messageBoxStartPos = new Size(boxStartX + 2, boxStartY + 1);
            this.mainBoxSize = new Size(boxSize.getColumns() - 4, boxSize.getRows() - 20);
            this.inputBoxStartPos = new Size(boxStartX + 2, this.messageBoxStartPos.getRows() + this.mainBoxSize.getRows());
            this.quitBoxPos = new Size(boxStartX + 2, this.inputBoxStartPos.getRows() + this.mainBoxSize.getRows());
            this.confirmBoxPos = new Size(this.quitBoxPos.getColumns() + this.mainBoxSize.getColumns() / 2 + 2, this.quitBoxPos.getRows());
            this.actionBoxSize = new Size(this.mainBoxSize.getColumns() / 2 - 2, 3);
        }

        public Size getOutterBoxStartPos() { return outterBoxStartPos; }
        public Size getMessageBoxStartPos() { return messageBoxStartPos; }
        public Size getMainBoxSize() { return mainBoxSize; }
        public Size getInputBoxStartPos() { return inputBoxStartPos; }
        public Size getQuitBoxPos() { return quitBoxPos; }
        public Size getConfirmBoxPos() { return confirmBoxPos; }
        public Size getActionBoxSize() { return actionBoxSize; }
    }

    private InterfaceManager() {
        try {
            this.terminal = TerminalBuilder.builder().system(true).build();
            this.terminalSize = terminal.getSize();
            this.boxSize = new Size(60, 30);
            this.renderBuffer = new StringBuilder(2048); // Pre-allocate a reasonable buffer size
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
                clearRenderBuffer();
                State.getCurrentState().render(terminalSize, boxSize);
                terminal.writer().print(renderBuffer);
                terminal.writer().flush();
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
                clearRenderBuffer();
                currentState.render(terminalSize, boxSize);
                terminal.writer().print(renderBuffer);
                terminal.writer().flush();

                int key = readKey();
                currentState.update(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearRenderBuffer() {
        renderBuffer.setLength(0);
    }

    public void clearScreen() {
        terminal.puts(InfoCmp.Capability.clear_screen);
        terminal.writer().flush();
    }

    private int readKey() throws IOException {
        return terminal.reader().read();
    }

    public void drawOutterBox(Size boxStartPos, Size boxSize, String instructions) {
        String horizontal = "─".repeat(boxSize.getColumns() - 2);

        // Top border
        renderBuffer.append(String.format("\033[%d;%dH┌%s┐%n", boxStartPos.getRows(), boxStartPos.getColumns(), horizontal));

        // Vertical borders
        for (int i = 1; i < boxSize.getRows() - 1; i++) {
            renderBuffer.append(String.format("\033[%d;%dH│%s│%n", boxStartPos.getRows() + i, boxStartPos.getColumns(), " ".repeat(boxSize.getColumns() - 2)));
        }

        // Dividing line
        int dividerY = boxStartPos.getRows() + boxSize.getRows() - 3;
        String dividerLine = "─".repeat(boxSize.getColumns() - 2);
        renderBuffer.append(String.format("\033[%d;%dH├%s┤%n", dividerY, boxStartPos.getColumns(), dividerLine));

        // Instructions
        renderBuffer.append(String.format("\033[%d;%dH%s%n", dividerY + 1, boxStartPos.getColumns() + 2, instructions));

        // Bottom border
        renderBuffer.append(String.format("\033[%d;%dH└%s┘%n", boxStartPos.getRows() + boxSize.getRows() - 1, boxStartPos.getColumns(), horizontal));
    }

    public void drawInnerBox(Size boxStartPos, Size boxSize, String content, boolean isInputBox) {
        String horizontal = "─".repeat(boxSize.getColumns() - 2);

        // Top border
        renderBuffer.append(String.format("\033[%d;%dH┌%s┐%n", boxStartPos.getRows(), boxStartPos.getColumns(), horizontal));

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

            renderBuffer.append(String.format("\033[%d;%dH│%s│%n", boxStartPos.getRows() + i, boxStartPos.getColumns(), paddedLine));
        }

        // Bottom border
        renderBuffer.append(String.format("\033[%d;%dH└%s┘%n", boxStartPos.getRows() + boxSize.getRows() - 1, boxStartPos.getColumns(), horizontal));
    }

    public void drawActionBox(Size boxStartPos, Size boxSize, String content) {
        String horizontal = "─".repeat(boxSize.getColumns() - 2);

        // Top border
        renderBuffer.append(String.format("\033[%d;%dH┌%s┐%n", boxStartPos.getRows(), boxStartPos.getColumns(), horizontal));

        // Vertical borders with centered content
        int contentStart = (boxSize.getColumns() - 2 - content.length()) / 2;
        String emptyLine = " ".repeat(boxSize.getColumns() - 2);

        for (int i = 1; i < boxSize.getRows() - 1; i++) {
            String line = emptyLine;
            if (i == boxSize.getRows() / 2) {
                line = " ".repeat(contentStart) + content + " ".repeat(boxSize.getColumns() - 2 - contentStart - content.length());
            }
            renderBuffer.append(String.format("\033[%d;%dH│%s│%n", boxStartPos.getRows() + i, boxStartPos.getColumns(), line));
        }

        // Bottom border
        renderBuffer.append(String.format("\033[%d;%dH└%s┘%n", boxStartPos.getRows() + boxSize.getRows() - 1, boxStartPos.getColumns(), horizontal));
    }

    public Terminal getTerminal() { return terminal; }
    public Size getTerminalSize() { return terminalSize; }
    public Size getBoxSize() { return boxSize; }
}