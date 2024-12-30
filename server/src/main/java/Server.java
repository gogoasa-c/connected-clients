import common.Logger;
import common.Student;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.reflect.Modifier.isStatic;

public class Server {
    public static final Server INSTANCE = new Server();
    public static final int PORT = 8080;
    public static final Logger log = Logger.getLogger();
    private final List<Student> students = new ArrayList<>();

    private Server() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::saveStudentsToFile));
    }

    public void serve() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            log.info("Server is listening on port " + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                log.info("New client connected.");
                new Thread(() -> handleClient(socket)).start();
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            saveStudentsToFile();
        }
    }

    private void handleClient(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String clientName = in.readLine();
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

    private void saveStudentsToFile() {
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String fileName = "attendance_" + date + ".json";
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("[\n");
            for (int i = 0; i < students.size(); i++) {
                Student student = students.get(i);
                writer.write("  {");
                Field[] fields = student.getClass().getDeclaredFields();
                int numberOfStaticFields = 0;
                int nonStaticFieldCount = 0;
                for (Field field : fields) {
                    if (!isStatic(field.getModifiers())) {
                        nonStaticFieldCount++;
                    }
                }
                int currentField = 0;
                for (Field field : fields) {
                    if (!isStatic(field.getModifiers())) {
                        field.setAccessible(true);
                        writer.write("\"" + field.getName() + "\": \"" + field.get(student) + "\"");
                        currentField++;
                        if (currentField < nonStaticFieldCount) {
                            writer.write(", ");
                        }
                    }
                }
                writer.write("}");
                if (i < students.size() - 1) {
                    writer.write(",");
                }
                writer.write("\n");
            }
            writer.write("]");
            log.info("Student data saved to " + fileName);
        } catch (IOException | IllegalAccessException e) {
            log.error("Failed to save student data to file: " + e.getMessage());
        }
    }
}