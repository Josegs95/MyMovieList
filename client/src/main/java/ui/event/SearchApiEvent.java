package ui.event;

import model.dto.MultimediaSummaryDTO;

import java.util.List;

public record SearchApiEvent(List<MultimediaSummaryDTO> multimediaList) implements Event {
}
