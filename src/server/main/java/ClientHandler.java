import database.Database;
import exception.DatabaseException;
import io.MessageType;
import io.SocketCommunication;
import json.JSONMessageProtocol;

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
                case REGISTER -> registerUser();
                case CREATE_USER_LIST -> createUserList();
                case RENAME_USER_LIST -> renameUserList();
                case DELETE_USER_LIST -> deleteUserList();
                case GET_USER_LISTS -> {
                    List<Map<String, Object>> userLists = getUserLists();
                    serverResponseData.put("lists", JSONMessageProtocol.serializeObject(userLists));

                }
                case ADD_MULTIMEDIA -> addMultimediaToList();
                case REMOVE_MULTIMEDIA -> removeMultimediaFromList();
                default -> System.out.println("Tipo de mensaje desconocido: " + messageType);
            }

            // Borrar
            System.out.println("Mensaje tipo \"" + messageType + "\" finalizado con éxito");

            status = 200;
            socketCommunication.writeToClient(serverResponseData, status, messageType);
        } catch (AuthenticationException | IOException e) {
            throw new RuntimeException(e);
        } catch (DatabaseException |SQLException e) {
            status = 500;

            if (e instanceof DatabaseException) {
                serverResponseData.put("error_code", ((DatabaseException) e).getErrorCode());
                serverResponseData.put("error_message", e.getMessage());
            } else {
                serverResponseData.put("error_message", "Unknown error in the server database");
            }

            try {
                socketCommunication.writeToClient(serverResponseData, status, messageType);
                if (!(e instanceof DatabaseException)) {
                    throw new RuntimeException(e);
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void registerUser() throws SQLException, DatabaseException {
        String username = clientData.get("username").toString();
        String password = clientData.get("password").toString();
        String email = Optional.ofNullable(clientData.get("email"))
                .map(Object::toString)
                .orElse(null);
        System.out.println("Un usuario se quiere registrar");

        if (!Database.registerUser(username, password, email)) {
            throw new DatabaseException("Couldn't register the user cause unknown reasons");
        }
    }

    private Integer loginUser() throws DatabaseException {
        String username = clientData.get("username").toString();
        String password = clientData.get("password").toString();
        System.out.printf("El usuario '%s' quiere identificarse%n", username);

        return Database.loginUser(username, password);
    }

    private void createUserList() throws AuthenticationException,
            SQLException, DatabaseException {
        String username = clientData.get("username").toString();
        Integer token = (Integer) clientData.get("token");
        int idUser = Database.validateUser(username, token);

        String listName = clientData.get("listName").toString();

        if (!Database.createUserList(idUser, listName)) {
            throw new DatabaseException("Couldn't create the user list cause unknown reasons");
        }
    }

    private void renameUserList() throws AuthenticationException, SQLException, DatabaseException {
        String username = clientData.get("username").toString();
        Integer token = (Integer) clientData.get("token");
        int idUser = Database.validateUser(username, token);

        String oldListName = clientData.get("oldListName").toString();
        String newListName = clientData.get("newListName").toString();

        Database.renameUserList(idUser, oldListName, newListName);
    }

    private void deleteUserList() throws AuthenticationException, SQLException, DatabaseException {
        String username = clientData.get("username").toString();
        Integer token = (Integer) clientData.get("token");
        int idUser = Database.validateUser(username, token);

        String listName = clientData.get("listName").toString();

        if (!Database.deleteUserList(idUser, listName)) {
            throw new DatabaseException("The list didn't get removed cause unknown reasons");
        }
    }

    private List<Map<String, Object>> getUserLists()
            throws DatabaseException, AuthenticationException {
        String username = clientData.get("username").toString();
        Integer token = (Integer) clientData.get("token");
        int idUser = Database.validateUser(username, token);

        return Database.getUserLists(idUser);
    }

    @SuppressWarnings({"unckecked", "unchecked"})
    private void addMultimediaToList() throws AuthenticationException,
            SQLException, DatabaseException {
        // User verification
        String username = clientData.get("username").toString();
        Integer token = (Integer) clientData.get("token");
        int idUser = Database.validateUser(username, token);

        // Verify if multimedia exists
        Map<String, Object> multimediaData = (Map<String, Object>) (clientData.get("multimedia"));
        int apiId = (int) (multimediaData.get("apiId"));
        String multimediaType = multimediaData.get("type").toString();

        int idMultimedia = Database.existMultimedia(apiId, multimediaType);
        if (idMultimedia == -1) {
            String title = multimediaData.get("title").toString();
            int totalEpisodes = (int) (multimediaData.get("totalEpisodes"));

            idMultimedia = Database.createMultimedia(apiId, title, multimediaType, totalEpisodes);

            if (idMultimedia != -1) {
                System.out.println("Multimedia creado en la BD de forma exitosa.");
            }
        }

        // Add multimedia to the list
        String listName = clientData.get("listName").toString();
        String status = clientData.get("status").toString();
        int currentEpisode = (int) (clientData.get("currentEpisode"));

        if (!Database.addMultimediaToList(idUser, idMultimedia, listName, status, currentEpisode)) {
            throw new DatabaseException("Couldn't add the multimedia to the user list cause unknown reasons.");
        }
    }

    @SuppressWarnings("unchecked")
    private void removeMultimediaFromList() throws AuthenticationException, SQLException, DatabaseException {
        // User verification
        String username = clientData.get("username").toString();
        Integer token = (Integer) clientData.get("token");
        int idUser = Database.validateUser(username, token);

        // Get Multimedia ID
        Map<String, Object> multimediaData = (Map<String, Object>) (clientData.get("multimedia"));
        int apiId = (int) (multimediaData.get("apiId"));
        String multimediaType = multimediaData.get("type").toString();
        int idMultimedia = Database.existMultimedia(apiId, multimediaType);

        if (idMultimedia == -1) {
            return;
        }

        // Remove multimedia from the list
        String listName = clientData.get("listName").toString();

        if (!Database.removeMultimediaFromList(idUser, idMultimedia, listName)) {
            throw new DatabaseException("Couldn't remove the multimedia from the user list cause unknown reasons.");
        }
    }
}
