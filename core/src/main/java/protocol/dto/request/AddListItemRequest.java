package protocol.dto.request;

import model.dto.MultimediaListItemDTO;

public record AddListItemRequest(Long idUser, MultimediaListItemDTO multimedia, String sessionToken) {
}
