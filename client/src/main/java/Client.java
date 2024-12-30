import common.Logger;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final Logger log = Logger.getLogger();
    public static final int PORT = 8080;

    public void connect() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        try (Socket socket = new Socket("localhost", PORT);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {
            log.info("Connected to the server on port " + PORT);
            out.println(name);
            log.info("Sent name to the server: " + name);
        } catch (IOException e) {
            log.error("Client exception: " + e.getMessage());
        }
    }
}