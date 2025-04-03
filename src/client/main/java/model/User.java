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
