import controller.APIController;
import file.ApplicationProperty;
import io.MessageFactory;
import io.MessageType;
import io.SocketCommunication;
import security.Security;
import view.MainFrameView;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Client {

    public Client() {
        ApplicationProperty.getProperties();
    }

    public static void main(String[] args) {
//        new Client().init();
        new Client().testCommunication();
    }

    private void testCommunication() {
        int port = Integer.parseInt(ApplicationProperty.getProperties().get("PORT"));
        String host = ApplicationProperty.getProperties().get("HOST");
        try(Socket socket = new Socket(host, port);
            SocketCommunication socketCommunication = new SocketCommunication(socket)) {

            Map<String, Object> data = new HashMap<>();
            data.put("name", "Jose");
            data.put("password", Security.hashString("12345"));

            socketCommunication.writeToServer(MessageFactory.createMessage(MessageType.TEST, data));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void init() {
        SwingUtilities.invokeLater(() -> {
            new MainFrameView();
            new Thread(() -> {
                try {
                    APIController.setUpConfigurationDetails();
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        });
    }
}
