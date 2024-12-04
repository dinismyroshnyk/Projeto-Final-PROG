package main.core;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal.Signal;

import java.io.IOException;

public class InterfaceManager {
    public void drawFirstUserCreationUI() {
        try (Terminal terminal = TerminalBuilder.builder().system(true).build()) {
            terminal.enterRawMode();
            terminal.puts(InfoCmp.Capability.clear_screen);

            // Instructions
            String instructions = "Press 'q' to quit";

            // Initial terminal size
            Size termSize = terminal.getSize();

            // Box dimensions
            Size boxSize = new Size(60, 30);

            // Redraw the UI on resize
            terminal.handle(Signal.WINCH, _ -> {
                Size newTermSize = terminal.getSize();
                termSize.copy(newTermSize);
                redrawUI(terminal, termSize, boxSize, instructions);
            });

            // Initial draw
            redrawUI(terminal, termSize, boxSize, instructions);

            // redraw UI
            while (true) {
                String keySequence = readKeySequence(terminal);

                redrawUI(terminal, termSize, boxSize, instructions);

                if (keySequence.equals("q")) {
                    terminal.puts(InfoCmp.Capability.clear_screen);
                    terminal.writer().flush();
                    System.exit(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readKeySequence(Terminal terminal) throws IOException {
        StringBuilder sequence = new StringBuilder();

        // First read
        int firstChar = terminal.reader().read();
        if (firstChar == -1) {
            return "";
        }
        sequence.append((char) firstChar);

        // Check for additional bytes in the sequence
        while (terminal.reader().ready()) {
            int nextChar = terminal.reader().read();
            if (nextChar == -1) break;
            sequence.append((char) nextChar);
        }

        return sequence.toString();
    }

    private void redrawUI(Terminal terminal, Size termSize, Size boxSize, String instructions) {
        int boxStartX = (termSize.getColumns() - boxSize.getColumns()) / 2;
        int boxStartY = (termSize.getRows() - boxSize.getRows()) / 2;
        Size outterBoxStartPos = new Size(boxStartX, boxStartY);

        terminal.puts(InfoCmp.Capability.clear_screen);
        drawOutterBox(terminal, outterBoxStartPos, boxSize, instructions);

        Size messageBoxStartPos = new Size(outterBoxStartPos.getColumns() + 2, outterBoxStartPos.getRows() + 1);
        Size mainBoxSize = new Size(boxSize.getColumns() - 4, boxSize.getRows() - 20);
        drawInnerBox(terminal, messageBoxStartPos, mainBoxSize, "No admin user found !\nCreating admin...", false);

        Size inputBoxStartPos = new Size(outterBoxStartPos.getColumns() + 2, messageBoxStartPos.getRows() + mainBoxSize.getRows());
        drawInnerBox(terminal, inputBoxStartPos, mainBoxSize, "username: [          ]\npassword: [          ]", true);

        // Bottom action boxes
        Size quitBoxPos = new Size(outterBoxStartPos.getColumns() + 2, inputBoxStartPos.getRows() + mainBoxSize.getRows());
        Size confirmBoxPos = new Size(quitBoxPos.getColumns() + mainBoxSize.getColumns() / 2 + 2, quitBoxPos.getRows());
        Size actionBoxSize = new Size(mainBoxSize.getColumns() / 2 - 2, 3);
        drawActionBox(terminal, quitBoxPos, actionBoxSize, "QUIT");
        drawActionBox(terminal, confirmBoxPos, actionBoxSize, "CONFIRM");

        terminal.writer().flush();
    }

    private static void drawOutterBox(Terminal terminal, Size boxStartPos, Size boxSize, String instructions) {
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

    private static void drawInnerBox(Terminal terminal, Size boxStartPos, Size boxSize, String content, boolean isInputBox) {
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

    private static void drawActionBox(Terminal terminal, Size boxStartPos, Size boxSize, String content) {
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
}