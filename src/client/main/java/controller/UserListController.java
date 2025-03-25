package controller;

import model.Multimedia;
import model.MultimediaList;
import model.User;
import view.MainFrame;

import java.util.HashMap;
import java.util.Map;

public class UserListController {

    UserListController(){
    }

    public void fetchUserList(User user){
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", user.getUsername());
    }

    public void createUserList(User user){}

    public void modifyMultimediaAttributes(User user, Multimedia multimedia){}

    public void deleteMultimediaFromList(User user, MultimediaList list, Multimedia multimedia){}

    public void showMultimediaDetails(MainFrame mainView){}
}
