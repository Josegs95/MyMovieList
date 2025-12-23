package ui.event;

import model.dto.UserListDTO;

public record DeleteListEvent(UserListDTO userList) implements Event {
}
