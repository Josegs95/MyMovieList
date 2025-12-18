package protocol.dto.request;

import model.dto.MultimediaListItemDTO;
import protocol.AuthCredentials;

public record AddListItemRequest(AuthCredentials auth, MultimediaListItemDTO multimedia) {
}
