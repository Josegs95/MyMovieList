package model;

import java.util.Set;

public class UserList {
    private final String listName;
    private final Set<MultimediaAtList> multimediaList;

    public UserList(String name, Set<MultimediaAtList> multimediaList){
        this.listName = name;
        this.multimediaList = multimediaList;
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
