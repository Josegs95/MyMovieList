import controller.ApiController;
import file.ApplicationProperty;
import view.MainFrame;
import view.component.dialog.ConfigureMultimediaDialog;

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
