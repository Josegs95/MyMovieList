import database.Database;
import exception.DatabaseException;
import io.MessageType;
import io.SocketCommunication;
import json.JSONMessageProtocol;

import java.io.IOException;
import java.net.Socket;
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
                    serverResponseData.put("login", Database.loginUser(clientData));
                }
                case REGISTER -> {
                    System.out.println("Un usuario se quiere registrar");
                    if (Database.registerUser(clientData))
                        System.out.println("Un usuario se ha registrado exitosamente");
                }
                default -> System.out.println("Tipo de mensaje desconocido: " + messageType);
            }

            socketCommunication.writeToClient(serverResponseData, 200, messageType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DatabaseException e) {
            try {
                serverResponseData.put("error_message", "Error relacionado con la base de datos del servidor. " +
                        "Intentelo de nuevo mas tarde.");
                socketCommunication.writeToClient(serverResponseData,500, messageType);
                socketCommunication.close();

                throw new RuntimeException(e);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
