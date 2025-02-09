import controller.APIController;
import file.ApplicationProperty;
import view.MainFrameView;

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
