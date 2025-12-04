package controller;

import dto.UserDTO;
import dto.UserListDTO;
import protocol.Message;
import model.TvShow;
import model.entity.*;
import protocol.MessageType;
import protocol.SocketCommunication;
import ui.view.MainFrame;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserListController {

    public UserListController() {}

    public static Message fetchUserList(UserDTO user) {
        Map<String, Object> userData = getUserData(user);

        return SocketCommunication.sendMessageToServer(new Message(MessageType.GET_USER_LISTS, userData));
    }

    public static Message createUserList(UserDTO user, String listName) {
        Map<String, Object> userData = getUserData(user);
        userData.put("listName", listName);

        try(SocketCommunication socketCommunication = new SocketCommunication()) {

            return SocketCommunication.sendMessageToServer(new Message(MessageType.CREATE_USER_LIST, userData));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Message renameUserList(UserDTO user, String oldListName, String newListName) {
        Map<String, Object> userData = getUserData(user);
        userData.put("oldListName", oldListName);
        userData.put("newListName", newListName);

        try(SocketCommunication socketCommunication = new SocketCommunication()) {
            return SocketCommunication.sendMessageToServer(new Message(MessageType.RENAME_USER_LIST, userData));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Message deleteUserList(UserDTO user, String listName) {
        Map<String, Object> userData = getUserData(user);
        userData.put("listName", listName);

        try(SocketCommunication socketCommunication = new SocketCommunication()) {
            return SocketCommunication.sendMessageToServer(new Message(MessageType.DELETE_USER_LIST, userData));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Message addMultimediaToList(UserDTO user, UserList userList, MultimediaListItem multimediaListItem) {
        Map<String, Object> userData = getUserData(user);
        Map<String, Object> multimediaData = new HashMap<>();

        Multimedia multimedia = multimediaListItem.getMultimedia();
        multimediaData.put("apiId", multimedia.getId());
        multimediaData.put("title", multimedia.getTitle());
        multimediaData.put("type", multimedia.getMultimediaType());
        if (multimedia.getMultimediaType() == MultimediaType.MOVIE) {
            multimediaData.put("totalEpisodes", 1);
        } else {
            multimediaData.put("totalEpisodes", ((TvShow) multimedia).getTotalEpisodes());
        }

        userData.put("multimedia", multimediaData);
        userData.put("listName", userList.getName());
        userData.put("status", multimediaListItem.getStatus());
        userData.put("currentEpisode", multimediaListItem.getCurrentEpisode());

        try(SocketCommunication socketCommunication = new SocketCommunication()) {
            return SocketCommunication.sendMessageToServer(new Message(MessageType.ADD_MULTIMEDIA, userData));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Message modifyMultimediaAttributes(UserDTO user, UserList userList,
                                                                  MultimediaListItem multimediaListItem) {
        Map<String, Object> userData = getUserData(user);
        Map<String, Object> multimediaData = new HashMap<>();

        Multimedia multimedia = multimediaListItem.getMultimedia();
        multimediaData.put("apiId", multimedia.getId());
        multimediaData.put("title", multimedia.getTitle());
        multimediaData.put("type", multimedia.getMultimediaType());
        if (multimedia.getMultimediaType() == MultimediaType.MOVIE) {
            multimediaData.put("totalEpisodes", 1);
        } else {
            multimediaData.put("totalEpisodes", ((TvShow) multimedia).getTotalEpisodes());
        }

        userData.put("multimedia", multimediaData);
        userData.put("listName", userList.getName());
        userData.put("status", multimediaListItem.getStatus());
        userData.put("currentEpisode", multimediaListItem.getCurrentEpisode());

        try(SocketCommunication socketCommunication = new SocketCommunication()) {
            return SocketCommunication.sendMessageToServer(new Message(MessageType.MODIFY_MULTIMEDIA, userData));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Message deleteMultimediaFromList(UserDTO user, UserListDTO userList, Multimedia multimedia) {
        Map<String, Object> userData = getUserData(user);
        Map<String, Object> multimediaData = new HashMap<>();

        multimediaData.put("apiId", multimedia.getId());
        multimediaData.put("type", multimedia.getMultimediaType());

        userData.put("multimedia", multimediaData);
        userData.put("listName", userList.name());

        try(SocketCommunication socketCommunication = new SocketCommunication()) {
            return SocketCommunication.sendMessageToServer(new Message(MessageType.REMOVE_MULTIMEDIA, userData));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void showMultimediaDetails(MainFrame mainView) {}

    private static Map<String, Object> getUserData(UserDTO user) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", user.getUsername());
        userData.put("token", user.getSessionToken());

        return userData;
    }
}
