package controller;

import io.MessageType;
import io.SocketCommunication;
import model.*;
import view.MainFrame;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserListController {

    public UserListController() {}

    public static ServerResponse fetchUserList(User user) {
        Map<String, Object> userData = getUserData(user);

        try(SocketCommunication socketCommunication = new SocketCommunication()) {

            return socketCommunication.writeToServer(userData, MessageType.GET_USER_LISTS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ServerResponse createUserList(User user, String listName) {
        Map<String, Object> userData = getUserData(user);
        userData.put("listName", listName);

        try(SocketCommunication socketCommunication = new SocketCommunication()) {

            return socketCommunication.writeToServer(userData, MessageType.CREATE_USER_LIST);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ServerResponse renameUserList(User user, String oldListName, String newListName) {
        Map<String, Object> userData = getUserData(user);
        userData.put("oldListName", oldListName);
        userData.put("newListName", newListName);

        try(SocketCommunication socketCommunication = new SocketCommunication()) {

            return socketCommunication.writeToServer(userData, MessageType.RENAME_USER_LIST);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ServerResponse deleteUserList(User user, String listName) {
        Map<String, Object> userData = getUserData(user);
        userData.put("listName", listName);

        try(SocketCommunication socketCommunication = new SocketCommunication()) {

            return socketCommunication.writeToServer(userData, MessageType.DELETE_USER_LIST);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ServerResponse addMultimediaToList(User user, UserList userList, MultimediaListItem multimediaListItem) {
        Map<String, Object> userData = getUserData(user);
        Map<String, Object> multimediaData = new HashMap<>();

        Multimedia multimedia = multimediaListItem.getMultimedia();
        multimediaData.put("apiId", multimedia.getId());
        multimediaData.put("title", multimedia.getTitle());
        multimediaData.put("type", multimedia.getMultimediaType());
        if (multimedia instanceof Movie) {
            multimediaData.put("totalEpisodes", 1);
        } else {
            multimediaData.put("totalEpisodes", ((TvShow) multimedia).getTotalEpisodes());
        }

        userData.put("multimedia", multimediaData);
        userData.put("listName", userList.getListName());
        userData.put("status", multimediaListItem.getStatus());
        userData.put("currentEpisode", multimediaListItem.getCurrentEpisode());

        try(SocketCommunication socketCommunication = new SocketCommunication()) {

            return socketCommunication.writeToServer(userData, MessageType.ADD_MULTIMEDIA);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void modifyMultimediaAttributes(User user, Multimedia multimedia) {}

    public static ServerResponse deleteMultimediaFromList(User user, UserList userList, Multimedia multimedia) {
        Map<String, Object> userData = getUserData(user);
        Map<String, Object> multimediaData = new HashMap<>();

        multimediaData.put("apiId", multimedia.getId());
        multimediaData.put("type", multimedia.getMultimediaType());

        userData.put("multimedia", multimediaData);
        userData.put("listName", userList.getListName());

        try(SocketCommunication socketCommunication = new SocketCommunication()) {

            return socketCommunication.writeToServer(userData, MessageType.REMOVE_MULTIMEDIA);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void showMultimediaDetails(MainFrame mainView) {}

    private static Map<String, Object> getUserData(User user) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", user.getUsername());
        userData.put("token", user.getSessionToken());

        return userData;
    }
}
