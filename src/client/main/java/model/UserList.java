package model;

import java.util.Set;

public class UserList {
    private String listName;
    private final Set<MultimediaListItem> multimediaList;

    public UserList(String name, Set<MultimediaListItem> multimediaList){
        this.listName = name;
        this.multimediaList = multimediaList;
    }

    public boolean removeMultimedia(Multimedia multimedia) {
        for (MultimediaListItem multimediaListItem : multimediaList) {
            if (multimediaListItem.getMultimedia().equals(multimedia)) {
                return multimediaList.remove(multimediaListItem);
            }
        }

        return false;
    }

    public String getListName() {
        return listName;
    }

    public String getFullListName() {
        return listName + " (" + multimediaList.size() + " items)";
    }

    public Set<MultimediaListItem> getMultimediaList() {
        return multimediaList;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    @Override
    public String toString() {
        return listName;
    }
}
