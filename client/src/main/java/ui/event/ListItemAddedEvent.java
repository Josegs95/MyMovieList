package ui.event;

import dto.MultimediaListItemDTO;
import dto.UserListDTO;

public record ListItemAddedEvent(UserListDTO userListDTO, MultimediaListItemDTO listItemDTO) implements Event {
}
