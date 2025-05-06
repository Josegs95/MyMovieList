package model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private final String username;
    private final Integer sessionToken;

    private List<UserList> lists;

    public User(String username, Integer sessionToken) {
        this.username = username;
        this.sessionToken = sessionToken;
    }

    public List<UserList> getUserListsWhichContainsMultimedia(Multimedia multimedia){
        List<UserList> auxLists = new ArrayList<>();

        userListsLoop :
        for (UserList userList : lists) {
            for (MultimediaListItem multimediaListItem : userList.getMultimediaList()) {
                if (multimediaListItem.getMultimedia().equals(multimedia)) {
                    auxLists.add(userList);
                    continue userListsLoop;
                }
            }
        }

        return auxLists;
    }

    public boolean hasMultimediaInAllList(Multimedia multimedia){
        return lists.size() == getUserListsWhichContainsMultimedia(multimedia).size();
    }

    public void setLists(List<UserList> lists) {
        this.lists = lists;
    }

    public String getUsername() {
        return username;
    }

    public List<UserList> getLists() {
        return lists;
    }

    public Integer getSessionToken() {
        return sessionToken;
    }
}
