import init.EnvironmentVariables;
import ui.controller.MainFrameUIController;
import ui.view.MainFrame;

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
            MainFrame mainFrame = new MainFrame();
            new MainFrameUIController(mainFrame);
        });
    }
}
