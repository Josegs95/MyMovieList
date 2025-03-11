import io.MessageType;
import io.SocketCommunication;
import json.JSONMessageProtocol;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
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
        try(SocketCommunication socketCommunication = new SocketCommunication(SOCKET)){
            String clientMessage = socketCommunication.readStringFromSocket();
            Map<String, Object> data = JSONMessageProtocol.createMapFromJSONString(clientMessage);
            if (data.get("message_type").equals(MessageType.KNOCK)){
                return;
            }

            Map<String, Object> response = new HashMap<>();
            socketCommunication.writeToClient(response, 200);

            clientMessage = socketCommunication.readStringFromSocket();
            data = JSONMessageProtocol.createMapFromJSONString(clientMessage);
            MessageType messageType = MessageType.valueOf(data.get("message_type").toString());
            switch (messageType){
                case TEST -> System.out.println("Es un mensaje de tipo Test");
                case LOGIN -> System.out.println("Es un mensaje de tipo Login");
                case REGISTER -> System.out.println("Es un mensaje de tipo Register");
                default -> System.out.println("Tipo de mensaje desconocido: " + messageType);
            }

            socketCommunication.writeToClient(response, 200);
        } catch (SocketException _){}
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
