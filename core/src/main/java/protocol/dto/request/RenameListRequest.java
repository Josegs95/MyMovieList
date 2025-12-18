package protocol.dto.request;

import protocol.AuthCredentials;

public record RenameListRequest(AuthCredentials auth, Long listId, String newListName) {
}
