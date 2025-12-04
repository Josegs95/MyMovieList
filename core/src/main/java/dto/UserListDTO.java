package dto;

import model.entity.UserList;

import java.util.List;
import java.util.stream.Collectors;

public record UserListDTO(Long id, String name, List<MultimediaListItemDTO> listItems) {

    public UserListDTO(UserList userList) {
        this(
                userList.getId(),
                userList.getName(),
                userList.getMultimediaList().stream()
                        .map(MultimediaListItemDTO::new)
                        .collect(Collectors.toList())
        );
    }
}
