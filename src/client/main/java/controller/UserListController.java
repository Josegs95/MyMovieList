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

    public static void deleteMultimediaFromList(User user, UserList list, Multimedia multimedia) {}

    public static void showMultimediaDetails(MainFrame mainView) {}

    private static Map<String, Object> getUserData(User user) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", user.getUsername());
        userData.put("token", user.getSessionToken());

        return userData;
    }
}
