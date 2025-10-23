package io;

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
    private final Socket socket;
    private final DataInputStream dis;
    private final DataOutputStream dos;

    public SocketCommunication(Socket socket) {
        if (socket == null)
            throw new NullPointerException("The object 'socket' can not be null");

        this.socket = socket;
        try {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SocketCommunication() throws IOException {
        this(new Socket(System.getProperty("SERVER_HOST"), Integer.parseInt(System.getProperty("SERVER_PORT"))));
    }

    public ServerResponse writeToServer(Map<String, Object> messageData, MessageType messageType)
            throws IOException {
        writeStringToSocket(MessageFactory.createMessage(MessageType.KNOCK, new HashMap<>()));
        ServerResponse serverResponse = new ServerResponse(readStringFromSocket());
        if (serverResponse.getMessageType() != MessageType.KNOCK
                || serverResponse.getStatus() != 200)
            return null;

        writeStringToSocket(MessageFactory.createMessage(messageType, messageData));

        return new ServerResponse(readStringFromSocket());
    }

    public void writeToClient(Map<String, Object> messageData, int status, MessageType messageType)
            throws IOException {
        writeStringToSocket(MessageFactory.createMessage(messageType, messageData, status));
    }

    public void writeToClient(int status, MessageType messageType) throws IOException {
        writeToClient(new HashMap<>(), status, messageType);
    }

    public String readStringFromSocket() throws IOException {
        String encodedMessage = dis.readUTF();
        return new String (Base64.getDecoder().decode(encodedMessage), StandardCharsets.UTF_8);
    }

    private void writeStringToSocket(String message) throws IOException {
        String encodedMessage = Base64.getEncoder().encodeToString(
                message.getBytes(StandardCharsets.UTF_8));
        dos.writeUTF(encodedMessage);
    }

    @Override
    public void close() throws IOException {
        dis.close();
        dos.close();
        socket.close();
    }
}
