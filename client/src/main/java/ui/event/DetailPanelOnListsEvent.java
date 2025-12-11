package ui.event;

import model.dto.MultimediaDetailDTO;
import model.dto.MultimediaSummaryDTO;
import model.dto.UserListDTO;

public record DetailPanelOnListsEvent(
        MultimediaDetailDTO multimediaDetail,
        MultimediaSummaryDTO multimediaSummary,
        UserListDTO userList)
        implements Event{
}
