package protocol.dto.response;

import model.dto.MultimediaSummaryDTO;

import java.util.List;

public record SearchMultimediaResponse(String baseImageUrl, List<MultimediaSummaryDTO> results) {
}
