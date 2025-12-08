package model.dto;

import model.entity.UserList;

import java.util.List;
import java.util.stream.Collectors;

public class UserListDTO {

    private Long id;
    private String name;
    private List<MultimediaListItemDTO> listItems;

    public UserListDTO() {
    }

    public UserListDTO(UserList userList) {
        this.id = userList.getId();
        this.name = userList.getName();
        this.listItems = userList.getMultimediaList().stream()
                        .map(MultimediaListItemDTO::new)
                        .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MultimediaListItemDTO> getListItems() {
        return listItems;
    }

    public void setListItems(List<MultimediaListItemDTO> listItems) {
        this.listItems = listItems;
    }
}
