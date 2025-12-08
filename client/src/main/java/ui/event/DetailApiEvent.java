package ui.event;

import model.dto.MultimediaDetailDTO;
import model.dto.MultimediaSummaryDTO;

public record DetailApiEvent(MultimediaDetailDTO multimediaDetail, MultimediaSummaryDTO multimediaSummary) implements Event{
}
