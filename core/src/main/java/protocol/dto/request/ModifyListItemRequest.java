package protocol.dto.request;

import model.dto.MultimediaListItemDTO;

public record ModifyListItemRequest(Long userId, MultimediaListItemDTO listItemDTO, String sessionToken) {
}
