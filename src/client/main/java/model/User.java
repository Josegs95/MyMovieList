package model;

import java.util.List;

public class User {
    private String username;
    private List<MultimediaList> lists;

    public User(String username) {
        this.username = username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLists(List<MultimediaList> lists) {
        this.lists = lists;
    }

    public String getUsername() {
        return username;
    }

    public List<MultimediaList> getLists() {
        return lists;
    }
}
