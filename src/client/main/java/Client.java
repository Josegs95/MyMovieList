import controller.ApiController;
import file.ApplicationProperty;
import view.MainFrame;

import javax.swing.*;
import java.io.IOException;

public class Client {

    public Client() {
        ApplicationProperty.getProperties();
    }

    public static void main(String[] args) {
        new Client().init();
    }

    private void init() {
        SwingUtilities.invokeLater(() -> {
            new MainFrame();
            new Thread(() -> {
                try {
                    ApiController.setUpConfigurationDetails();
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        });
    }
}
