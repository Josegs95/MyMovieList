package ui.util;

import exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.ErrorType;

import javax.naming.CommunicationException;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandler.class);

    private final static String DEFAULT_SERVER_ERROR_MESSAGE = "Error desconocido del servidor";
    private static final Map<ErrorType, String> ERROR_MESSAGE_MAP = new HashMap<>();

    static {
        ERROR_MESSAGE_MAP.put(ErrorType.INVALID_CREDENTIALS, "Usuario o contraseña incorrectos");
        ERROR_MESSAGE_MAP.put(ErrorType.SESSION_EXPIRED, "Sesión expirada. Autentíquese de nuevo por favor");
        ERROR_MESSAGE_MAP.put(ErrorType.SESSION_NOT_FOUND, "No existe la sesión");
        ERROR_MESSAGE_MAP.put(ErrorType.LIST_NOT_BELONG_USER, "Error, esta lista no te pertenece");
        ERROR_MESSAGE_MAP.put(ErrorType.USER_NOT_FOUND, "Error, usuario no encontrado");
        ERROR_MESSAGE_MAP.put(ErrorType.USER_LIST_NOT_FOUND, "Error, no se ha encontrado la lista del usuario");
        ERROR_MESSAGE_MAP.put(ErrorType.MULTIMEDIA_NOT_FOUND_IN_LIST, "Error, no se encuentra el objeto multimedia en la lista");
        ERROR_MESSAGE_MAP.put(ErrorType.USERNAME_ALREADY_EXISTS, "Ya existe un usuario con ese nombre de usuario");
        ERROR_MESSAGE_MAP.put(ErrorType.LIST_NAME_ALREADY_EXISTS_FOR_USER, "Ya tienes una lista con ese nombre");
        ERROR_MESSAGE_MAP.put(ErrorType.MULTIMEDIA_ALREADY_IN_LIST, "Este objeto multimedia ya está en la lista");
        ERROR_MESSAGE_MAP.put(ErrorType.API_RELATED_ERROR, "Error desconocido relacionado con la API");
    }

    public static void showError(Component parent, Exception e) {
        String errorMessage;
        String messageTitle;

        if (e instanceof CommunicationException) {
            errorMessage = "Error al intentar comunicarse con el servidor";
            messageTitle = "Error de comunicación";
        }
        else if (e instanceof ServerException se) {
            errorMessage = ERROR_MESSAGE_MAP.getOrDefault(se.getErrorType(), DEFAULT_SERVER_ERROR_MESSAGE);
            messageTitle = "Error";

            LOGGER.error(e.getMessage());
        }
        else {
            errorMessage = "Error desconocido";
            messageTitle = "Error";

            LOGGER.error(e.getMessage(), e);
        }

        JOptionPane.showMessageDialog(parent, errorMessage, messageTitle,JOptionPane.ERROR_MESSAGE);
    }
}
