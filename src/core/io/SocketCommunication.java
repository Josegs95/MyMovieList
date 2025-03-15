package io;

import file.ApplicationProperty;
import model.ServerResponse;

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

    public SocketCommunication() throws IOException {
        this(new Socket(ApplicationProperty.getHOST(), ApplicationProperty.getPORT()));
    }

    public ServerResponse writeToServer(Map<String, Object> messageData, MessageType messageType) throws IOException {
        writeStringToSocket(MessageFactory.createMessage(MessageType.KNOCK, new HashMap<>()));
        ServerResponse serverResponse = new ServerResponse(readStringFromSocket());
        if (serverResponse.getMessageType() != MessageType.KNOCK || serverResponse.getStatus() != 200)
            return null;

        writeStringToSocket(MessageFactory.createMessage(messageType, messageData));

        return new ServerResponse(readStringFromSocket());
    }

    public void writeToClient(Map<String, Object> messageData, int status, MessageType messageType) throws IOException {
        writeStringToSocket(MessageFactory.createMessage(messageType, messageData, status));
    }

    public void writeToClient(int status, MessageType messageType) throws IOException {
        writeToClient(new HashMap<>(), status, messageType);
    }

    public String readStringFromSocket() throws IOException {
        String encodedMessage = DIS.readUTF();
        return new String (Base64.getDecoder().decode(encodedMessage), StandardCharsets.UTF_8);
    }

    private void writeStringToSocket(String message) throws IOException {
        String encodedMessage = Base64.getEncoder().encodeToString(message.getBytes(StandardCharsets.UTF_8));
        DOS.writeUTF(encodedMessage);
    }

    @Override
    public void close() throws IOException {
        DIS.close();
        DOS.close();
        SOCKET.close();
    }
}
