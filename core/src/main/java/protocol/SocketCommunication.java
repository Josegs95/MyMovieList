package protocol;

import model.Message;
import tools.jackson.databind.ObjectMapper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

public class SocketCommunication implements AutoCloseable {

    private final Socket socket;
    private final DataInputStream dis;
    private final DataOutputStream dos;

    private final ObjectMapper objectMapper;

    public SocketCommunication(Socket socket) {
        if (socket == null)
            throw new NullPointerException("The object 'socket' can not be null");

        objectMapper = new ObjectMapper();
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

    public Message writeToServer(MessageType messageType, Map<String, Object> messageData)
            throws IOException {
        writeStringToSocket(objectMapper.writeValueAsString(new Message(MessageType.KNOCK, null, null)));
        Message serverResponse = objectMapper.readValue(readStringFromSocket(), Message.class);

        if (serverResponse.messageType() != MessageType.KNOCK
                || serverResponse.status() == null || serverResponse.status() != 200)
            return null;

        writeStringToSocket(objectMapper.writeValueAsString(new Message(messageType, null, messageData)));

        return objectMapper.readValue(readStringFromSocket(), Message.class);
    }

    public void writeToClient(MessageType messageType, Long status, Map<String, Object> messageData)
            throws IOException {
        writeStringToSocket(objectMapper.writeValueAsString(new Message(messageType, status, messageData)));
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
