package service;

import exception.CommunicationException;
import model.Message;
import model.entity.User;
import protocol.MessageType;
import protocol.SocketCommunication;
import security.Security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuthService {

    public User login(String username, String password) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("password", Security.hashString(password));

        Message serverMessage = writeMessage(userData, MessageType.LOGIN);

        return new User(username, (Integer) serverMessage.content().get("token"));
    }

    public void register(String username, String password, String email) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("password", Security.hashString(password));
        userData.put("email", email);

        writeMessage(userData, MessageType.REGISTER);
    }

    private Message writeMessage(Map<String, Object> userData, MessageType messageType) {
        Message serverMessage;
        try (SocketCommunication socketCommunication = new SocketCommunication()) {
            serverMessage = socketCommunication.writeToServer(messageType, userData);
        } catch (IOException e) {
            throw new CommunicationException("Error al intentar comunicarse al servidor");
        }

        if (serverMessage.status() == null || serverMessage.status() != 200L) {
            String errorMessage = serverMessage.getErrorMessage().orElse("Error desconocido");
            throw new RuntimeException(errorMessage);
        }

        return serverMessage;
    }
}
