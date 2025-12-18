package protocol.dto.request;

import protocol.AuthCredentials;

public record DeleteListRequest(AuthCredentials auth, Long listId) {
}
