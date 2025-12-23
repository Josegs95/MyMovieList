import context.SessionContext;
import init.EnvironmentVariables;
import ui.controller.MainFrameUIController;
import ui.event.LogoutEvent;
import ui.util.EventBus;
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
        EventBus.subscribe(LogoutEvent.class, this::restartApplication);
        SwingUtilities.invokeLater(this::startNewSession);
    }

    private void startNewSession() {
        MainFrame mainFrame = new MainFrame();
        new MainFrameUIController(mainFrame);

        if (!mainFrame.doInitialLogin()) {
            mainFrame.dispose();
            throw new RuntimeException("The client closed the application");
        }
    }

    private void restartApplication(LogoutEvent event) {
        EventBus.reset();
        SessionContext.getInstance().reset();

        init();
    }
}
