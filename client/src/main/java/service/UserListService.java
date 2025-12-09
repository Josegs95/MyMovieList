package service;

import context.SessionContext;
import model.dto.MultimediaListItemDTO;
import model.dto.UserDTO;
import model.dto.UserListDTO;
import protocol.Message;
import protocol.MessageType;
import protocol.SocketCommunication;
import protocol.dto.request.*;
import protocol.dto.response.GetAllListsResponse;
import tools.jackson.databind.ObjectMapper;
import ui.event.*;
import ui.util.EventBus;

public class UserListService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final UserDTO user = SessionContext.getInstance().getUser();

    public void createList(String listName) {
        CreateListRequest request = new CreateListRequest(user.getId(), listName, user.getSessionToken());

        Message serverMessage = SocketCommunication.sendMessageToServer(new Message(MessageType.CREATE_USER_LIST, request));
        UserListDTO userList = mapper.convertValue(serverMessage.content(), UserListDTO.class);
        user.getLists().add(userList);

        EventBus.publish(new CreateListEvent(userList));
    }

    public void getAllListsWithItems() {
        GetAllListsRequest request = new GetAllListsRequest(user.getId(), user.getSessionToken());

        Message serverMessage = SocketCommunication.sendMessageToServer(new Message(MessageType.GET_USER_LISTS, request));
        GetAllListsResponse response = mapper.convertValue(serverMessage.content(), GetAllListsResponse.class);
        user.setLists(response.lists());

        EventBus.publish(new GetListsEvent(response.lists()));
    }

    public void renameList(UserListDTO userList, String newListName) {
        RenameListRequest request = new RenameListRequest(user.getId(), userList.getId(), newListName, user.getSessionToken());

        Message serverMessage = SocketCommunication.sendMessageToServer(new Message(MessageType.RENAME_USER_LIST, request));
        UserListDTO renamedUserList = mapper.convertValue(serverMessage.content(), UserListDTO.class);
        userList.setName(renamedUserList.getName());

        EventBus.publish(new RenameListEvent(newListName));
    }

    public void deleteList(UserListDTO userListDTO) {
        DeleteListRequest request = new DeleteListRequest(user.getId(), userListDTO.getId(), user.getSessionToken());

        SocketCommunication.sendMessageToServer(new Message(MessageType.DELETE_USER_LIST, request));
        user.getLists().remove(userListDTO);

        EventBus.publish(new DeleteListEvent(userListDTO));
    }

    public void addItemToList(MultimediaListItemDTO multimedia) {
        AddListItemRequest request = new AddListItemRequest(user.getId(), multimedia, user.getSessionToken());

        Message serverMessage = SocketCommunication.sendMessageToServer(new Message(MessageType.ADD_MULTIMEDIA, request));
        MultimediaListItemDTO listItem = mapper.convertValue(serverMessage.content(), MultimediaListItemDTO.class);

        UserListDTO userListDTO = user.getLists().stream()
                .filter(list -> list.getId().equals(multimedia.getListId()))
                .findFirst().orElseThrow();
        userListDTO.getListItems().add(listItem);

        EventBus.publish(new AddListItemEvent(userListDTO, listItem));
    }

    public void modifyItemList(MultimediaListItemDTO multimedia) {
        ModifyListItemRequest request = new ModifyListItemRequest(user.getId(), multimedia, user.getSessionToken());

        Message serverMessage = SocketCommunication.sendMessageToServer(new Message(MessageType.MODIFY_MULTIMEDIA, request));
        MultimediaListItemDTO listItem = mapper.convertValue(serverMessage.content(), MultimediaListItemDTO.class);

        UserListDTO userListDTO = user.getLists().stream()
                .filter(list -> list.getId().equals(multimedia.getListId()))
                .findFirst().orElseThrow();
        MultimediaListItemDTO oldListItem = userListDTO.getListItems().stream()
                .filter(item -> item.getMultimedia().getIdDb().equals(listItem.getMultimedia().getIdDb()))
                .findFirst().orElseThrow();
        oldListItem.setCurrentEpisode(listItem.getCurrentEpisode());
        oldListItem.setStatus(listItem.getStatus());

        EventBus.publish(new ModifyListItemEvent(listItem));
    }

    public void deleteItemFromList(UserListDTO userListDTO, MultimediaListItemDTO listItem) {
        DeleteItemListRequest request = new DeleteItemListRequest(
                user.getId(),
                userListDTO.getId(),
                listItem.getMultimedia().getIdDb(),
                user.getSessionToken());

        SocketCommunication.sendMessageToServer(new Message(MessageType.REMOVE_MULTIMEDIA, request));
        userListDTO.getListItems().remove(listItem);

        EventBus.publish(new DeleteListItemEvent(userListDTO, listItem));
    }
}
