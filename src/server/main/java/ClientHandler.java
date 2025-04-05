import database.Database;
import exception.DatabaseException;
import io.MessageType;
import io.SocketCommunication;
import json.JSONMessageProtocol;
import model.UserList;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ClientHandler implements Runnable{
    final private Socket SOCKET;

    private Map<String, Object> clientData;

    public ClientHandler(Socket socket){
        if (socket == null)
            throw new NullPointerException("The object 'socket' can not be null");

        this.SOCKET = socket;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        SocketCommunication socketCommunication = new SocketCommunication(SOCKET);
        MessageType messageType = null;
        int status;
        Map<String, Object> serverResponseData = new HashMap<>();
        try{
            String clientMessage = socketCommunication.readStringFromSocket();
            Map<String, Object> messageData =
                    JSONMessageProtocol.createMapFromJSONString(clientMessage);
            if (messageData.get("message_type").equals(MessageType.KNOCK)){
                return;
            }

            socketCommunication.writeToClient(200, MessageType.KNOCK);

            clientMessage = socketCommunication.readStringFromSocket();
            messageData = JSONMessageProtocol.createMapFromJSONString(clientMessage);
            messageType = MessageType.valueOf(messageData.get("message_type").toString());
            clientData = (Map<String, Object>) messageData.get("data");

            System.out.println("From client: " + clientMessage);

            switch (messageType){
                case TEST -> System.out.println("Es un mensaje de tipo Test");
                case LOGIN -> {
                    Integer sessionToken = loginUser();
                    if (sessionToken == null)
                        serverResponseData.put("login", false);
                    else {
                        serverResponseData.put("login", true);
                        serverResponseData.put("token", sessionToken);
                    }
                }
                case REGISTER -> {
                    if (registerUser())
                        System.out.println("Un usuario se ha registrado exitosamente");
                }
                case CREATE_USER_LIST -> {
                    if (createUserList())
                        System.out.println("Se ha creado una lista de multimedia nueva");
                }
                case GET_USER_LISTS -> {
                    List<UserList> userLists = getUserLists();
                    serverResponseData.put("lists", JSONMessageProtocol.serializeObject(userLists));

                }
                default -> System.out.println("Tipo de mensaje desconocido: " + messageType);
            }

            status = 200;
            socketCommunication.writeToClient(serverResponseData, status, messageType);
        } catch (AuthenticationException | IOException e) {
            throw new RuntimeException(e);
        } catch (DatabaseException |SQLException e) {
            if (e instanceof DatabaseException)
                serverResponseData.put("error_code", ((DatabaseException) e).getErrorCode());
            else
                serverResponseData.put("error_message", "Unknown error in the server database");
            status = 500;
            try {
                socketCommunication.writeToClient(serverResponseData, status, messageType);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    private boolean registerUser() throws SQLException, DatabaseException {
        String username = clientData.get("username").toString();
        String password = clientData.get("password").toString();
        String email = Optional.ofNullable(clientData.get("email"))
                .map(Object::toString)
                .orElse(null);
        System.out.println("Un usuario se quiere registrar");

        return Database.registerUser(username, password, email);
    }

    private Integer loginUser() throws DatabaseException {
        String username = clientData.get("username").toString();
        String password = clientData.get("password").toString();
        System.out.printf("El usuario '%s' quiere identificarse%n", username);

        return Database.loginUser(username, password);
    }

    private boolean createUserList() throws AuthenticationException,
            SQLException, DatabaseException {
        String username = clientData.get("username").toString();
        Integer token = (Integer) clientData.get("token");
        String listName = clientData.get("listName").toString();
        int idUser = Database.validateUser(username, token);

        return Database.createUserList(idUser, listName);
    }

    private List<UserList> getUserLists() throws DatabaseException, AuthenticationException {
        String username = clientData.get("username").toString();
        Integer token = (Integer) clientData.get("token");
        int idUser = Database.validateUser(username, token);

        return Database.getUserLists(idUser);
    }
}
