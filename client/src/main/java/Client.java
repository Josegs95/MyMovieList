import controller.ApiController;
import init.EnvironmentVariables;
import view.MainFrame;

import javax.swing.*;

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
            new Thread(ApiController::setUpConfigurationDetails).start();
        });
    }
}
