package protocol.dto.request;

import model.dto.MultimediaListItemDTO;

public record AddItemListRequest(Long idUser, MultimediaListItemDTO multimedia, Integer sessionToken) {
}
