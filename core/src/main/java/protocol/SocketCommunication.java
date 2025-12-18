package protocol;

import exception.CommunicationException;
import exception.ServerException;
import exception.SessionExpiredException;
import tools.jackson.databind.ObjectMapper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

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

    public static Message sendMessageToServer(Message clientMessage) {
        try (SocketCommunication socketCommunication = new SocketCommunication()) {
            Message serverMessage = socketCommunication.writeToServer(clientMessage);

            if (serverMessage.getStatus() == 200L) return serverMessage;

            ErrorDetails errorDetails = serverMessage.getErrorDetail();
            ErrorType type = errorDetails.getError();

            if (type == ErrorType.SESSION_EXPIRED) {
                throw new SessionExpiredException(errorDetails.getMessage());
            }

            throw new ServerException(type, errorDetails.getMessage());
        } catch (IOException e) {
            throw new CommunicationException("Error al intentar comunicarse al servidor");
        }
    }

    private Message writeToServer(Message message)
            throws IOException {
        writeStringToSocket(objectMapper.writeValueAsString(new Message(MessageType.KNOCK, null, null)));
        Message serverResponse = objectMapper.readValue(readStringFromSocket(), Message.class);

        if (serverResponse.getMessageType() != MessageType.KNOCK || serverResponse.getStatus() != 200) {
            throw new RuntimeException("Error de comunicación con el servidor");
        }

        writeStringToSocket(objectMapper.writeValueAsString(message));

        return objectMapper.readValue(readStringFromSocket(), Message.class);
    }

    public void writeToClient(MessageType messageType, Long status, Object messageData, ErrorDetails errorDetails)
            throws IOException {
        Message message = new Message(messageType, status, messageData);
        message.setErrorDetail(errorDetails);
        writeStringToSocket(objectMapper.writeValueAsString(message));
    }

    public String readStringFromSocket() throws IOException {
        String encodedMessage = dis.readUTF();
        String decodedMessage = new String (Base64.getDecoder().decode(encodedMessage), StandardCharsets.UTF_8);
        System.out.printf("Mensaje leído: %s%n", decodedMessage);
        return decodedMessage;
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
