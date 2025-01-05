import common.Logger;
import common.Student;
import handler.ClientHandler;
import handler.FileHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static final Server INSTANCE = new Server();
    public static final int PORT = 8080;
    public static final Logger log = Logger.getLogger();

    private final List<Student> students = new ArrayList<>();
    private final ClientHandler clientHandler = new ClientHandler(students);

    private Server() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> FileHandler.saveStudentsToFile(this.students)));
    }

    public void serve() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            log.info("Server is listening on port " + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                log.info("New client connected.");
                new Thread(() -> clientHandler.handleClient(socket)).start();
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}