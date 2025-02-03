import file.ApplicationProperty;
import view.MainFrameView;

import javax.swing.*;
import java.nio.file.Path;
import java.util.Map;

public class Client {
    final private Map<String, String> PROPERTIES;

    public Client() {
        PROPERTIES = ApplicationProperty.getProperties();
    }

    public static void main(String[] args) {
        new Client().init();
    }

    private void init() {
        if (PROPERTIES == null)
            return;

        SwingUtilities.invokeLater(() -> {
            MainFrameView view = new MainFrameView();
        });
    }
}
