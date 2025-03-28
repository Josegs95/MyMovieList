package model;

import java.util.List;

public class User {
    final private String USERNAME;
    final private Integer SESSION_TOKEN;

    private List<UserList> lists;

    public User(String username, Integer sessionToken) {
        this.USERNAME = username;
        this.SESSION_TOKEN = sessionToken;
    }

    public void setLists(List<UserList> lists) {
        this.lists = lists;
    }

    public String getUsername() {
        return USERNAME;
    }

    public List<UserList> getLists() {
        return lists;
    }

    public Integer getSessionToken() {
        return SESSION_TOKEN;
    }
}
