package dto.response;

import dto.MultimediaSummaryDTO;

import java.util.List;

public record SearchMultimediaResponse(String baseImageUrl, List<MultimediaSummaryDTO> results) {
}
