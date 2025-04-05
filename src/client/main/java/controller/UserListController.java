package controller;

import io.MessageType;
import io.SocketCommunication;
import model.Multimedia;
import model.UserList;
import model.ServerResponse;
import model.User;
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

    public ServerResponse createUserList(User user, String listName) {
        Map<String, Object> userData = getUserData(user);
        userData.put("listName", listName);

        try(SocketCommunication socketCommunication = new SocketCommunication()) {

            return socketCommunication.writeToServer(userData, MessageType.CREATE_USER_LIST);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void modifyMultimediaAttributes(User user, Multimedia multimedia) {}

    public void deleteMultimediaFromList(User user, UserList list, Multimedia multimedia) {}

    public void showMultimediaDetails(MainFrame mainView) {}

    private static Map<String, Object> getUserData(User user) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", user.getUsername());
        userData.put("token", user.getSessionToken());

        return userData;
    }
}
