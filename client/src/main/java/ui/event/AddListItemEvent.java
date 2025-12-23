package ui.event;

import model.dto.MultimediaListItemDTO;
import model.dto.UserListDTO;

public record AddListItemEvent(UserListDTO userListDTO, MultimediaListItemDTO listItemDTO) implements Event {
}
