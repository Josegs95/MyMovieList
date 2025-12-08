package protocol.dto.response;

import model.dto.MultimediaDetailDTO;

public record MultimediaDetailResponse(String baseImageUrl, MultimediaDetailDTO multimedia) {
}
