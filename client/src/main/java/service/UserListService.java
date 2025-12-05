package service;

import context.SessionContext;
import dto.MultimediaListItemDTO;
import dto.UserDTO;
import dto.UserListDTO;
import dto.request.*;
import dto.response.GetAllListsResponse;
import protocol.Message;
import protocol.MessageType;
import protocol.SocketCommunication;
import tools.jackson.databind.ObjectMapper;
import ui.event.ListCreatedEvent;
import ui.event.ListItemAddedEvent;
import ui.event.ListItemDeletedEvent;
import ui.util.EventBus;

import java.util.List;

public class UserListService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final UserDTO user = SessionContext.getInstance().getUser();

    public UserListDTO createList(String listName) {
        CreateListRequest request = new CreateListRequest(user.getId(), listName, user.getSessionToken());

        Message serverMessage = SocketCommunication.sendMessageToServer(new Message(MessageType.CREATE_USER_LIST, request));
        UserListDTO userList = mapper.convertValue(serverMessage.content(), UserListDTO.class);
        user.getLists().add(userList);
        EventBus.publish(new ListCreatedEvent(userList));

        return userList;
    }

    public List<UserListDTO> getAllListsWithItems() {
        GetAllListsRequest request = new GetAllListsRequest(user.getId(), user.getSessionToken());

        Message serverMessage = SocketCommunication.sendMessageToServer(new Message(MessageType.GET_USER_LISTS, request));
        GetAllListsResponse response = mapper.convertValue(serverMessage.content(), GetAllListsResponse.class);
        user.setLists(response.lists());

        return response.lists();
    }

    public void renameList(UserListDTO userList, String newListName) {
        RenameListRequest request = new RenameListRequest(user.getId(), userList.getId(), newListName, user.getSessionToken());

        Message serverMessage = SocketCommunication.sendMessageToServer(new Message(MessageType.RENAME_USER_LIST, request));
        UserListDTO renamedUserList = mapper.convertValue(serverMessage.content(), UserListDTO.class);
        userList.setName(renamedUserList.getName());
    }

    public void addItemToList(UserListDTO userListDTO, MultimediaListItemDTO multimedia) {
        AddItemListRequest request = new AddItemListRequest(user.getId(), multimedia, user.getSessionToken());

        Message serverMessage = SocketCommunication.sendMessageToServer(new Message(MessageType.ADD_MULTIMEDIA, request));
        MultimediaListItemDTO listItem = mapper.convertValue(serverMessage.content(), MultimediaListItemDTO.class);
        userListDTO.getListItems().add(listItem);

        EventBus.publish(new ListItemAddedEvent(userListDTO, listItem));
    }

    public void deleteItemFromList(UserListDTO userListDTO, MultimediaListItemDTO listItem) {
        DeleteItemListRequest request = new DeleteItemListRequest(
                user.getId(),
                userListDTO.getId(),
                listItem.getMultimedia().getIdDb(),
                user.getSessionToken());

        SocketCommunication.sendMessageToServer(new Message(MessageType.REMOVE_MULTIMEDIA, request));
        userListDTO.getListItems().remove(listItem);

        EventBus.publish(new ListItemDeletedEvent(userListDTO, listItem));
    }
}
