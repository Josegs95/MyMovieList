package service;

import context.SessionContext;
import model.dto.MultimediaListItemDTO;
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
    private final SessionContext context = SessionContext.getInstance();

    public void createList(String listName) {
        CreateListRequest request = new CreateListRequest(context.getAuthCredentials(), listName);

        Message serverMessage = SocketCommunication.sendMessageToServer(new Message(MessageType.CREATE_USER_LIST, request));
        UserListDTO userList = mapper.convertValue(serverMessage.getContent(), UserListDTO.class);
        context.getUser().getLists().add(userList);

        EventBus.publish(new CreateListEvent(userList));
    }

    public void getAllListsWithItems() {
        GetAllListsRequest request = new GetAllListsRequest(context.getAuthCredentials());

        Message serverMessage = SocketCommunication.sendMessageToServer(new Message(MessageType.GET_USER_LISTS, request));
        GetAllListsResponse response = mapper.convertValue(serverMessage.getContent(), GetAllListsResponse.class);
        context.getUser().setLists(response.lists());

        EventBus.publish(new GetListsEvent(response.lists()));
    }

    public void renameList(UserListDTO userList, String newListName) {
        RenameListRequest request = new RenameListRequest(context.getAuthCredentials(), userList.getId(), newListName);

        Message serverMessage = SocketCommunication.sendMessageToServer(new Message(MessageType.RENAME_USER_LIST, request));
        UserListDTO renamedUserList = mapper.convertValue(serverMessage.getContent(), UserListDTO.class);
        userList.setName(renamedUserList.getName());

        EventBus.publish(new RenameListEvent(newListName));
    }

    public void deleteList(UserListDTO userListDTO) {
        DeleteListRequest request = new DeleteListRequest(context.getAuthCredentials(), userListDTO.getId());

        SocketCommunication.sendMessageToServer(new Message(MessageType.DELETE_USER_LIST, request));
        context.getUser().getLists().remove(userListDTO);

        EventBus.publish(new DeleteListEvent(userListDTO));
    }

    public void addItemToList(MultimediaListItemDTO multimedia) {
        AddListItemRequest request = new AddListItemRequest(context.getAuthCredentials(), multimedia);

        Message serverMessage = SocketCommunication.sendMessageToServer(new Message(MessageType.ADD_MULTIMEDIA, request));
        MultimediaListItemDTO listItem = mapper.convertValue(serverMessage.getContent(), MultimediaListItemDTO.class);

        UserListDTO userListDTO = context.getUser().getLists().stream()
                .filter(list -> list.getId().equals(multimedia.getListId()))
                .findFirst().orElseThrow();
        userListDTO.getListItems().add(listItem);

        EventBus.publish(new AddListItemEvent(userListDTO, listItem));
    }

    public void modifyItemList(MultimediaListItemDTO multimedia) {
        ModifyListItemRequest request = new ModifyListItemRequest(context.getAuthCredentials(), multimedia);

        Message serverMessage = SocketCommunication.sendMessageToServer(new Message(MessageType.MODIFY_MULTIMEDIA, request));
        MultimediaListItemDTO listItem = mapper.convertValue(serverMessage.getContent(), MultimediaListItemDTO.class);

        UserListDTO userListDTO = context.getUser().getLists().stream()
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
                context.getAuthCredentials(),
                userListDTO.getId(),
                listItem.getMultimedia().getIdDb());

        SocketCommunication.sendMessageToServer(new Message(MessageType.REMOVE_MULTIMEDIA, request));
        userListDTO.getListItems().remove(listItem);

        EventBus.publish(new DeleteListItemEvent(userListDTO, listItem));
    }
}
