package protocol.dto.request;

import protocol.AuthCredentials;

public record CreateListRequest(AuthCredentials auth, String listName) {
}
