package model;

import java.util.List;

public class User {
    final private String USERNAME;
    final private Integer SESSION_TOKEN;

    private List<MultimediaList> lists;

    public User(String username, Integer sessionToken) {
        this.USERNAME = username;
        this.SESSION_TOKEN = sessionToken;
    }

    public void setLists(List<MultimediaList> lists) {
        this.lists = lists;
    }

    public String getUsername() {
        return USERNAME;
    }

    public List<MultimediaList> getLists() {
        return lists;
    }
}
