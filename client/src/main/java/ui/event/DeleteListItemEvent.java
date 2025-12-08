package ui.event;

import model.dto.MultimediaListItemDTO;
import model.dto.UserListDTO;

public record DeleteListItemEvent(UserListDTO userListDTO, MultimediaListItemDTO deletedItem) implements Event {
}
