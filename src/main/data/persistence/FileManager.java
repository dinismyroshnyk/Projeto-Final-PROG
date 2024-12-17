package main.data.persistence;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileManager {
    private static final String DATA_FOLDER = "data";
    private static final String CREDENTIALS_FILE = DATA_FOLDER + "/credenciais_acesso.txt";
    private static FileManager instance;

    private FileManager() {
        try {
            Files.createDirectories(Paths.get(DATA_FOLDER));
        } catch (IOException e) {
            System.err.println("Error creating data directory: " + e.getMessage());
        }
    }

    public static FileManager getInstance() {
        if (instance == null) {
            instance = new FileManager();
        }
        return instance;
    }

    public void saveCredentials(String username, String password) {
        Path file = Paths.get(CREDENTIALS_FILE);
        String content = "username=" + username + System.lineSeparator() + "password=" + password + System.lineSeparator() + System.lineSeparator();
        ByteBuffer buffer = ByteBuffer.wrap(content.getBytes(StandardCharsets.UTF_8));

        try (FileChannel fileChannel = FileChannel.open(file,
            StandardOpenOption.CREATE,
            StandardOpenOption.WRITE,
            StandardOpenOption.APPEND)){

            fileChannel.write(buffer);

        } catch (IOException e) {
            System.err.println("Error saving credentials to file: " + e.getMessage());
        }
    }
}