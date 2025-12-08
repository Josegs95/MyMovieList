import dao.*;
import exception.*;
import model.dto.MultimediaListItemDTO;
import model.dto.UserListDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.Message;
import protocol.MessageType;
import protocol.SocketCommunication;
import protocol.dto.request.*;
import protocol.dto.response.GetAllListsResponse;
import protocol.dto.response.LoginResponse;
import protocol.dto.response.MultimediaDetailResponse;
import protocol.dto.response.SearchMultimediaResponse;
import service.*;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class ClientHandler implements Runnable{

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientHandler.class);

    private static final UserDAO USER_DAO = new UserDAOImpl();
    private static final UserListDAO USER_LIST_DAO = new UserListDAOImpl();
    private static final MultimediaDAO MULTIMEDIA_DAO = new MultimediaDAOImpl();
    private static final MultimediaListItemDAO MULTIMEDIA_LIST_ITEM_DAO = new MultimediaListItemDAOImpl();

    private static final AuthService AUTH_SERVICE = new AuthService(USER_DAO);
    private static final ApiService API_SERVICE = new ApiService(AUTH_SERVICE);
    private static final UserService USER_SERVICE = new UserService(USER_DAO);
    private static final UserListService USER_LIST_SERVICE = new UserListService(USER_LIST_DAO, AUTH_SERVICE);
    private static final MultimediaService MULTIMEDIA_SERVICE = new MultimediaService(MULTIMEDIA_DAO);
    private static final MultimediaListItemService MULTIMEDIA_LIST_ITEM_SERVICE = new MultimediaListItemService(
            MULTIMEDIA_LIST_ITEM_DAO,
            AUTH_SERVICE,
            USER_LIST_SERVICE,
            MULTIMEDIA_SERVICE
    );

    final private Socket SOCKET;

    private Object clientData;
    private final ObjectMapper mapper;

    public ClientHandler(Socket socket){
        if (socket == null) {
            throw new NullPointerException("The object 'socket' can not be null");
        }

        this.SOCKET = socket;
        mapper = new ObjectMapper();
    }

    @Override
    public void run() {
        SocketCommunication socketCommunication = new SocketCommunication(SOCKET);
        MessageType messageType = null;
        long status;
        Object serverResponseData = null;
        try{
            knockMessage(socketCommunication);

            String clientJSONMessage = socketCommunication.readStringFromSocket();
            Message clientMessage = mapper.readValue(clientJSONMessage, Message.class);
            messageType = clientMessage.messageType();
            clientData = clientMessage.content();

            switch (messageType){
                case TEST -> LOGGER.info("Ha llegado un mensaje de tipo Test");
                case LOGIN -> serverResponseData = loginUser();
                case REGISTER -> registerUser();
                case CREATE_USER_LIST -> serverResponseData = createUserList();
                case RENAME_USER_LIST -> serverResponseData = renameUserList();
                case DELETE_USER_LIST -> deleteUserList();
                case GET_USER_LISTS -> serverResponseData = getUserLists();
                case ADD_MULTIMEDIA -> serverResponseData = addMultimediaToList();
//                case MODIFY_MULTIMEDIA -> serverResponseData = new HashMap<>(modifyMultimedia());
                case REMOVE_MULTIMEDIA -> removeMultimediaFromList();
                case API_SEARCH_MULTIMEDIA -> serverResponseData = searchMultimedia();
                case API_DETAIL_MULTIMEDIA -> serverResponseData = detailMultimedia();
                default -> {
                    LOGGER.warn("Tipo de mensaje desconocido: {}", messageType);
                    throw new RuntimeException("Error en el protocolo del mensaje: mensaje desconocido");
                }
            }

            status = 200L;
        } catch (ServerException e) {
            status = switch (e) {
                case ValidationException _ -> 400L;
                case AuthenticationException _ -> 401L;
                case AuthorizationException _ -> 403L;
                case ResourceNotFoundException _ -> 404L;
                case OperationNotAllowedException _ -> 405L;
                case ConflictException _ -> 409L;
                default -> throw new IllegalStateException("Unexpected value: " + e);
            };

            serverResponseData = Map.of("error_message", e.getMessage());
            LOGGER.info(e.getMessage());
        } catch (Exception e) {
            status = 500L;
            String errorMessage = "Error interno del servidor";
            serverResponseData = Map.of("error_message", errorMessage);
            LOGGER.error(errorMessage, e);
        }

        try {
            socketCommunication.writeToClient(messageType, status, serverResponseData);
        } catch (IOException ex) {
            LOGGER.error("Couldn't send the message. Disconnected client: {}", ex.getMessage());
        }
    }

    private void knockMessage(SocketCommunication socketCommunication) throws IOException {
        Message clientMessage = mapper.readValue(socketCommunication.readStringFromSocket(), Message.class);
        if (clientMessage.messageType() != MessageType.KNOCK){
            throw new RuntimeException("Message with unknown comm protocol");
        }

        socketCommunication.writeToClient(MessageType.KNOCK, 200L, null);
    }

    private void registerUser() {
        RegisterRequest request = mapper.convertValue(clientData, RegisterRequest.class);

        USER_SERVICE.register(request);
    }

    private LoginResponse loginUser() {
        LoginRequest request = mapper.convertValue(clientData, LoginRequest.class);
        LOGGER.info("El usuario '{}' quiere identificarse", request.username());

        return new LoginResponse(USER_SERVICE.login(request));
    }

    private SearchMultimediaResponse searchMultimedia() {
        SearchMultimediaRequest request = mapper.convertValue(clientData, SearchMultimediaRequest.class);

        return API_SERVICE.searchAllByName(request);
    }

    private MultimediaDetailResponse detailMultimedia() {
        MultimediaDetailRequest request = mapper.convertValue(clientData, MultimediaDetailRequest.class);

        return API_SERVICE.getMultimediaDetails(request);
    }

    private UserListDTO createUserList() {
        CreateListRequest request = mapper.convertValue(clientData, CreateListRequest.class);

        return USER_LIST_SERVICE.create(request);
    }

    private UserListDTO renameUserList() {
        RenameListRequest request = mapper.convertValue(clientData, RenameListRequest.class);

        return USER_LIST_SERVICE.rename(
                request.userId(),
                request.listId(),
                request.newListName(),
                request.sessionToken());
    }

    private void deleteUserList() {
        DeleteListRequest request = mapper.convertValue(clientData, DeleteListRequest.class);

        USER_LIST_SERVICE.delete(request.userId(), request.listId(), request.sessionToken());
    }

    private GetAllListsResponse getUserLists() {
        GetAllListsRequest request = mapper.convertValue(clientData, GetAllListsRequest.class);

        return new GetAllListsResponse(USER_LIST_SERVICE.getAllListsFromUser(request.idUser(), request.sessionToken()));
    }

    private MultimediaListItemDTO addMultimediaToList(){
        AddItemListRequest request = mapper.convertValue(clientData, AddItemListRequest.class);

        return MULTIMEDIA_LIST_ITEM_SERVICE.addMultimediaToList(request.idUser(), request.multimedia(), request.sessionToken());
    }

//    @SuppressWarnings({"unckecked", "unchecked"})
//    private Map<String, Object> modifyMultimedia() throws AuthenticationException, SQLException, DatabaseException {
//        // User verification
//        String username = clientData.get("username").toString();
//        Integer token = (Integer) clientData.get("token");
//        int idUser = Database.validateUser(username, token);
//
//        // Get the multimedia ID
//        Map<String, Object> multimediaData = (Map<String, Object>) (clientData.get("multimedia"));
//        int apiId = (int) (multimediaData.get("apiId"));
//        String multimediaType = multimediaData.get("type").toString();
//
//        int idMultimedia = Database.existMultimedia(apiId, multimediaType);
//
//        // Modify multimedia
//        String listName = clientData.get("listName").toString();
//        String status = clientData.get("status").toString();
//        int currentEpisode = (int) (clientData.get("currentEpisode"));
//
//        Map<String, Object> result = Database.modifyMultimedia(idUser, idMultimedia, listName, status, currentEpisode);
//        if (result == null) {
//            throw new DatabaseException("Couldn't modify the multimedia cause unknown reasons.");
//        } else {
//            return result;
//        }
//    }

    private void removeMultimediaFromList() {
        DeleteItemListRequest request = mapper.convertValue(clientData, DeleteItemListRequest.class);

        MULTIMEDIA_LIST_ITEM_SERVICE.delete(
                request.idUser(),
                request.idList(),
                request.idMultimedia(),
                request.sessionToken());
    }
}
