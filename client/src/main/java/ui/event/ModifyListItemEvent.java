package ui.event;

import model.dto.MultimediaListItemDTO;

public record ModifyListItemEvent(MultimediaListItemDTO modifiedListItem) implements Event{
}
