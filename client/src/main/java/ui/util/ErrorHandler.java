package ui.util;

import exception.ServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.CommunicationException;
import javax.swing.*;
import java.awt.*;

public class ErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandler.class);

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
        if (e instanceof ServerException) {
            JOptionPane.showMessageDialog(
                    parent,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        LOGGER.error("Error desconocido", e);
        JOptionPane.showMessageDialog(
                parent,
                "Error desconocido",
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
