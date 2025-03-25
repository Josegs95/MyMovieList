import database.Database;
import exception.DatabaseException;
import io.MessageType;
import io.SocketCommunication;
import json.JSONMessageProtocol;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler implements Runnable{
    final private Socket SOCKET;

    public ClientHandler(Socket socket){
        if (socket == null)
            throw new NullPointerException("The object 'socket' can not be null");

        this.SOCKET = socket;
    }

    @Override
    public void run() {
        SocketCommunication socketCommunication = new SocketCommunication(SOCKET);
        MessageType messageType = null;
        int status = 0;
        Map<String, Object> serverResponseData = new HashMap<>();
        try{
            String clientMessage = socketCommunication.readStringFromSocket();
            Map<String, Object> messageData = JSONMessageProtocol.createMapFromJSONString(clientMessage);
            if (messageData.get("message_type").equals(MessageType.KNOCK)){
                return;
            }

            socketCommunication.writeToClient(200, MessageType.KNOCK);

            clientMessage = socketCommunication.readStringFromSocket();
            messageData = JSONMessageProtocol.createMapFromJSONString(clientMessage);
            messageType = MessageType.valueOf(messageData.get("message_type").toString());
            Map<String, Object> clientData = (Map<String, Object>) messageData.get("data");
            switch (messageType){
                case TEST -> System.out.println("Es un mensaje de tipo Test");
                case LOGIN -> {
                    System.out.println("Un usuario quiere identificarse");
                    Integer salt = Database.loginUser(clientData);
                    if (salt == null)
                        serverResponseData.put("login", false);
                    else {
                        serverResponseData.put("login", true);
                        serverResponseData.put("token", salt);
                    }
                }
                case REGISTER -> {
                    System.out.println("Un usuario se quiere registrar");
                    if (Database.registerUser(clientData))
                        System.out.println("Un usuario se ha registrado exitosamente");
                }
                default -> System.out.println("Tipo de mensaje desconocido: " + messageType);
            }

            status = 200;
        } catch (IOException e) {
            status = 500;
            throw new RuntimeException(e);
        } catch (DatabaseException e) {
            serverResponseData.put("error_message", e.getMessage());
            status = 500;
        } catch (SQLException e) {
            serverResponseData.put("error_message", "Error desconocido en la base de datos del servidor.");
            status = 500;
        } finally {
            try {
                socketCommunication.writeToClient(serverResponseData, status, messageType);
                socketCommunication.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
