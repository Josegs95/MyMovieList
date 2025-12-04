package ui.event;

import dto.UserListDTO;

import java.util.List;

public record GetListsEvent(List<UserListDTO> lists) implements Event{
}
