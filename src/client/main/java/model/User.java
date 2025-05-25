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

        this.lists = new ArrayList<>();
    }

    public List<UserList> getListsWithMultimedia(Multimedia multimedia){
        return lists.stream()
                .filter(userList -> userList.getMultimediaList().stream()
                        .anyMatch(item -> item.getMultimedia().equals(multimedia)))
                .toList();
    }

    public List<UserList> getListsWithoutMultimedia(Multimedia multimedia){
        return lists.stream()
                .filter(userList -> userList.getMultimediaList().stream()
                        .noneMatch(item -> item.getMultimedia().equals(multimedia)))
                .toList();
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
