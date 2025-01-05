package handler;

import common.Logger;
import common.Student;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;

public class ClientHandler {
    private static final Logger log = Logger.getLogger();
    private final List<Student> students;

    public ClientHandler(final List<Student> students) {
        this.students = students;
    }

    public void handleClient(Socket socket) {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String clientName = input.readLine();
            log.info(String.format("Client with name %s has successfully connected.", clientName));
            addStudent(new Student(clientName));
        } catch (IOException e) {
            log.error("Client handler exception: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                log.error("Failed to close client socket: " + e.getMessage());
            }
        }
    }

    public synchronized void addStudent(Student student) {
        students.add(student);
    }
}
