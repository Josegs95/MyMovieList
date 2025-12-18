package protocol.dto.request;

import protocol.AuthCredentials;

public record DeleteItemListRequest(AuthCredentials auth, Long idList, Long idMultimedia) {


}
