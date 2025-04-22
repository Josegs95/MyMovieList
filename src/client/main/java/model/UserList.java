package model;

import java.util.Set;

public class UserList {
    private final String listName;
    private final Set<MultimediaListItem> multimediaList;

    public UserList(String name, Set<MultimediaListItem> multimediaList){
        this.listName = name;
        this.multimediaList = multimediaList;
    }

    public String getListName() {
        return listName;
    }

    public Set<MultimediaListItem> getMultimediaList() {
        return multimediaList;
    }

    @Override
    public String toString() {
        return listName + " (" + multimediaList.size() + " items)";
    }
}
