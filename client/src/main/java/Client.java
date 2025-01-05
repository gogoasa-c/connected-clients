import common.Logger;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends Frame implements ActionListener {
    private final int PORT = 8080;
    private final Logger log = Logger.getLogger();

    private final TextField nameField;
    private Socket socket;
    private PrintWriter out;

    public Client() {
        setLayout(new FlowLayout());

        Label nameLabel = new Label("Enter your name:");
        add(nameLabel);

        nameField = new TextField(20);
        add(nameField);

        final Button submitButton = new Button("Submit");
        submitButton.addActionListener(this);
        add(submitButton);

        setTitle("Client");
        setSize(300, 100);
        setVisible(true);

        try {
            socket = new Socket("localhost", PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            log.error(e.getMessage());
            System.exit(1);
        }

        addWindowListener(new WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                try {
                    socket.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
                System.exit(0);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String name = nameField.getText();
        out.println(name);
        nameField.setText("");
        System.exit(0);
    }
}