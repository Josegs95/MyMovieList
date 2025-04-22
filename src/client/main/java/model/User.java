package model;

import java.util.List;

public class User {
    private final String username;
    private final Integer sessionToken;

    private List<UserList> lists;

    public User(String username, Integer sessionToken) {
        this.username = username;
        this.sessionToken = sessionToken;
    }

    public boolean hasMultimediaInAnyList(Multimedia multimedia){
        for (UserList userList : lists) {
            for (MultimediaListItem multimediaListItem : userList.getMultimediaList()) {
                if (multimediaListItem.getMultimedia().equals(multimedia)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean hasMultimediaInAllList(Multimedia multimedia){
        listsLoop :
        for (UserList userList : lists) {
            boolean found = false;
            for (MultimediaListItem multimediaListItem : userList.getMultimediaList()) {
                if (multimediaListItem.getMultimedia().equals(multimedia)) {
                    continue listsLoop;
                }
            }

            if (!found) {
                return false;
            }
        }

        return true;
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
