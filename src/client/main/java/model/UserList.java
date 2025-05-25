package model;

import java.util.Set;

public class UserList {

    private String listName;
    private final Set<MultimediaListItem> multimediaList;

    public UserList(String name, Set<MultimediaListItem> multimediaList){
        this.listName = name;
        this.multimediaList = multimediaList;
    }

    public void removeMultimediaListItem(MultimediaListItem mli) {
        multimediaList.remove(mli);
    }

    public MultimediaListItem getMultimediaListItem(Multimedia multimedia) {
        return multimediaList.stream()
                .filter(mli -> mli.getMultimedia().equals(multimedia))
                .findFirst().orElseThrow();
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
