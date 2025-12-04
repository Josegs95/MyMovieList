package dto.request;

import dto.MultimediaListItemDTO;

public record AddItemListRequest(Long idUser, MultimediaListItemDTO multimedia, Integer sessionToken) {
}
