package ui.event;

import dto.UserListDTO;

public record ListCreatedEvent(UserListDTO userList) implements Event{
}
