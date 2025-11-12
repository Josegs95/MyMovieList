package ui.util;

import javax.naming.CommunicationException;
import javax.swing.*;
import java.awt.*;

public class ErrorHandler {

    public static void showError(Component parent, Exception e) {
        if (e instanceof CommunicationException) {
            JOptionPane.showMessageDialog(
                    parent,
                    "Error al intentar comunicarse con el servidor",
                    "Error de comunicación",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        if (e instanceof RuntimeException) {
            JOptionPane.showMessageDialog(
                    parent,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        JOptionPane.showMessageDialog(
                parent,
                "Error desconocido",
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
