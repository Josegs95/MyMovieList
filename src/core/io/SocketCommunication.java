package io;

import json.JSONMessageProtocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class SocketCommunication implements AutoCloseable {
    final private Socket SOCKET;
    final private DataInputStream DIS;
    final private DataOutputStream DOS;

    public SocketCommunication(Socket socket) {
        if (socket == null)
            throw new NullPointerException("The object 'socket' can not be null");

        this.SOCKET = socket;
        try {
            DIS = new DataInputStream(socket.getInputStream());
            DOS = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean writeToServer(String message) throws IOException {
        writeStringToSocket(MessageFactory.createMessage(MessageType.KNOCK, new HashMap<>()));
        String serverMessage = readStringFromSocket();
        if (getServerStatus(serverMessage) != 200)
            return false;

        writeStringToSocket(message);
        serverMessage = readStringFromSocket();

        return getServerStatus(serverMessage) == 200;
    }

    public void writeToClient(Map<String, Object> messageData, int status) throws IOException {
        writeStringToSocket(MessageFactory.createMessage(MessageType.KNOCK, messageData, status));
    }

    private void writeStringToSocket(String message) throws IOException {
        String encodedMessage = Base64.getEncoder().encodeToString(message.getBytes(StandardCharsets.UTF_8));
        System.out.println("Escribiendo mensaje encriptado : " + encodedMessage);
        DOS.writeUTF(encodedMessage);
    }

    public String readStringFromSocket() throws IOException {
        String encodedMessage = DIS.readUTF();
        System.out.println("Leyendo mensaje encriptado : " + encodedMessage);
        return new String (Base64.getDecoder().decode(encodedMessage), StandardCharsets.UTF_8);
    }

    private int getServerStatus(String serverMessage){
        return (int) (JSONMessageProtocol.createMapFromJSONString(serverMessage).get("status"));
    }

    @Override
    public void close() throws IOException {
        DIS.close();
        DOS.close();
        SOCKET.close();
    }
}
