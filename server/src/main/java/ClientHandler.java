import dao.*;
import exception.ServerException;
import exception.UnexpectedMessageException;
import model.dto.MultimediaListItemDTO;
import model.dto.UserListDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.*;
import protocol.dto.request.*;
import protocol.dto.response.GetAllListsResponse;
import protocol.dto.response.LoginResponse;
import protocol.dto.response.MultimediaDetailResponse;
import protocol.dto.response.SearchMultimediaResponse;
import service.*;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable{

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientHandler.class);

    private static final ClientSessionDAO CLIENT_SESSION_DAO = new ClientSessionDAOImpl();
    private static final UserDAO USER_DAO = new UserDAOImpl();
    private static final UserListDAO USER_LIST_DAO = new UserListDAOImpl();
    private static final MultimediaDAO MULTIMEDIA_DAO = new MultimediaDAOImpl();
    private static final MultimediaListItemDAO MULTIMEDIA_LIST_ITEM_DAO = new MultimediaListItemDAOImpl();

    private static final AuthService AUTH_SERVICE = new AuthService(USER_DAO, CLIENT_SESSION_DAO);
    private static final ApiService API_SERVICE = new ApiService(AUTH_SERVICE);
    private static final UserService USER_SERVICE = new UserService(USER_DAO, AUTH_SERVICE);
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
    private AuthCredentials authCredentials;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public ClientHandler(Socket socket){
        if (socket == null) {
            throw new NullPointerException("The object 'socket' can not be null");
        }

        this.SOCKET = socket;
    }

    @Override
    public void run() {
        try (SocketCommunication socketCommunication = new SocketCommunication(SOCKET)) {
            MessageType messageType = null;
            long status;
            Object serverResponseData = null;
            ErrorDetails errorDetails = null;

            try {
                knockMessage(socketCommunication);

                String clientJSONMessage = socketCommunication.readStringFromSocket();
                Message clientMessage = MAPPER.readValue(clientJSONMessage, Message.class);
                messageType = clientMessage.getMessageType();
                clientData = clientMessage.getContent();

                switch (messageType) {
                    case TEST -> LOGGER.info("Ha llegado un mensaje de tipo Test");
                    case LOGIN -> serverResponseData = loginUser();
                    case REGISTER -> registerUser();
                    case CREATE_USER_LIST -> serverResponseData = createUserList();
                    case RENAME_USER_LIST -> serverResponseData = renameUserList();
                    case DELETE_USER_LIST -> deleteUserList();
                    case GET_USER_LISTS -> serverResponseData = getUserLists();
                    case ADD_MULTIMEDIA -> serverResponseData = addMultimediaToList();
                    case MODIFY_MULTIMEDIA -> serverResponseData = modifyMultimedia();
                    case REMOVE_MULTIMEDIA -> removeMultimediaFromList();
                    case API_SEARCH_MULTIMEDIA -> serverResponseData = searchMultimedia();
                    case API_DETAIL_MULTIMEDIA -> serverResponseData = detailMultimedia();
                    default -> {
                        LOGGER.warn("Tipo de mensaje desconocido: {}", messageType);
                        throw new RuntimeException("Error en el protocolo del mensaje: mensaje desconocido");
                    }
                }

                status = 200L;
            } catch (UnexpectedMessageException e) {
                LOGGER.warn(e.getMessage());
                return;
            } catch (ServerException e) {
                errorDetails = new ErrorDetails(ErrorType.fromException(e.getClass()), e.getMessage());
                status = errorDetails.getError().getStatusCode();
                LOGGER.info(e.getMessage());
            } catch (Exception e) {
                String errorMessage = "Error interno del servidor";
                errorDetails = new ErrorDetails(ErrorType.INTERNAL_SERVER_ERROR, errorMessage);
                status = errorDetails.getError().getStatusCode();
                LOGGER.error(errorMessage, e);
            }

            if (status == 200L && authCredentials != null) {
                AUTH_SERVICE.refreshSessionToken(authCredentials.sessionToken());
            }

            socketCommunication.writeToClient(messageType, status, serverResponseData, errorDetails);
        } catch (IOException ex) {
            LOGGER.error("Couldn't send the message. Disconnected client: {}", ex.getMessage());
        }
    }

    private void knockMessage(SocketCommunication socketCommunication) throws IOException {
        Message clientMessage = MAPPER.readValue(socketCommunication.readStringFromSocket(), Message.class);
        if (clientMessage.getMessageType() != MessageType.KNOCK){
            throw new UnexpectedMessageException("Expected KNOCK message but found " + clientMessage.getMessageType().name());
        }

        socketCommunication.writeToClient(MessageType.KNOCK, 200L, null, null);
    }

    private void registerUser() {
        RegisterRequest request = MAPPER.convertValue(clientData, RegisterRequest.class);

        USER_SERVICE.register(request.username(), request.password(), request.email());
    }

    private LoginResponse loginUser() {
        LoginRequest request = MAPPER.convertValue(clientData, LoginRequest.class);
        LOGGER.info("El usuario '{}' quiere identificarse", request.username());

        return new LoginResponse(USER_SERVICE.login(request.username(), request.password()));
    }

    private SearchMultimediaResponse searchMultimedia() {
        SearchMultimediaRequest request = MAPPER.convertValue(clientData, SearchMultimediaRequest.class);

        authCredentials = request.auth();

        return API_SERVICE.searchAllByName(authCredentials, request.searchText());
    }

    private MultimediaDetailResponse detailMultimedia() {
        MultimediaDetailRequest request = MAPPER.convertValue(clientData, MultimediaDetailRequest.class);

        authCredentials = request.auth();

        return API_SERVICE.getMultimediaDetails(authCredentials, request.apiId(), request.type());
    }

    private UserListDTO createUserList() {
        CreateListRequest request = MAPPER.convertValue(clientData, CreateListRequest.class);

        authCredentials = request.auth();

        return USER_LIST_SERVICE.create(authCredentials, request.listName());
    }

    private UserListDTO renameUserList() {
        RenameListRequest request = MAPPER.convertValue(clientData, RenameListRequest.class);

        authCredentials = request.auth();

        return USER_LIST_SERVICE.rename(authCredentials, request.listId(), request.newListName());
    }

    private void deleteUserList() {
        DeleteListRequest request = MAPPER.convertValue(clientData, DeleteListRequest.class);

        authCredentials = request.auth();

        USER_LIST_SERVICE.delete(authCredentials, request.listId());
    }

    private GetAllListsResponse getUserLists() {
        GetAllListsRequest request = MAPPER.convertValue(clientData, GetAllListsRequest.class);

        authCredentials = request.auth();

        return new GetAllListsResponse(USER_LIST_SERVICE.getAllListsFromUser(authCredentials));
    }

    private MultimediaListItemDTO addMultimediaToList(){
        AddListItemRequest request = MAPPER.convertValue(clientData, AddListItemRequest.class);

        authCredentials = request.auth();

        return MULTIMEDIA_LIST_ITEM_SERVICE.addMultimediaToList(authCredentials, request.multimedia());
    }

    private MultimediaListItemDTO modifyMultimedia() {
        ModifyListItemRequest request = MAPPER.convertValue(clientData, ModifyListItemRequest.class);

        authCredentials = request.auth();

        return MULTIMEDIA_LIST_ITEM_SERVICE.modify(authCredentials, request.listItemDTO());
    }

    private void removeMultimediaFromList() {
        DeleteItemListRequest request = MAPPER.convertValue(clientData, DeleteItemListRequest.class);

        authCredentials = request.auth();

        MULTIMEDIA_LIST_ITEM_SERVICE.delete(authCredentials, request.idList(), request.idMultimedia());
    }
}
