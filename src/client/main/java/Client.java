import controller.ApiController;
import init.EnvironmentVariables;
import view.MainFrame;

import javax.swing.*;
import java.io.IOException;

public class Client {

    public Client() {
        EnvironmentVariables.loadEnvironmentVariables();
    }

    public static void main(String[] args) {
        new Client().init();
    }

    private void init() {
        SwingUtilities.invokeLater(() -> {
            MainFrame.getInstance();
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
