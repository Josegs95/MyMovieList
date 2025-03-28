package model;

import java.util.Set;

public class UserList {
    final private String listName;
    final private Set<MultimediaAtList> multimediaList;

    public UserList(String name, Set<MultimediaAtList> multimediaSet){
        this.listName = name;
        this.multimediaList = multimediaSet;
    }

    public String getListName() {
        return listName;
    }

    public Set<MultimediaAtList> getMultimediaList() {
        return multimediaList;
    }

    @Override
    public String toString() {
        return listName + " (" + multimediaList.size() + " items)";
    }
}
