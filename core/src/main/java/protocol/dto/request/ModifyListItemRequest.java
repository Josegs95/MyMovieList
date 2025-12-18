package protocol.dto.request;

import model.dto.MultimediaListItemDTO;
import protocol.AuthCredentials;

public record ModifyListItemRequest(AuthCredentials auth, MultimediaListItemDTO listItemDTO) {
}
