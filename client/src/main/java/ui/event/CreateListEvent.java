package ui.event;

import model.dto.UserListDTO;

public record CreateListEvent(UserListDTO userList) implements Event{
}
