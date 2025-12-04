package dto;

import model.entity.User;

import java.util.List;

public class UserDTO {

    private Long id;
    private String username;
    private Integer sessionToken;
    private List<UserListDTO> lists;

    public UserDTO(){}

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.sessionToken = user.getSessionToken();
        this.lists = user.getMultimediaLists().stream()
                .map(UserListDTO::new)
                .toList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(Integer sessionToken) {
        this.sessionToken = sessionToken;
    }

    public List<UserListDTO> getLists() {
        return lists;
    }

    public void setLists(List<UserListDTO> lists) {
        this.lists = lists;
    }
}
