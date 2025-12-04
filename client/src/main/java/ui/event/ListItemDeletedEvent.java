package ui.event;

import dto.MultimediaListItemDTO;
import dto.UserListDTO;

public record ListItemDeletedEvent(UserListDTO userListDTO, MultimediaListItemDTO deletedItem) implements Event {
}
