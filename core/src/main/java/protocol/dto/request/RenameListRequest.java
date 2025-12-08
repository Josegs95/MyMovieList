package protocol.dto.request;

public record RenameListRequest(Long userId, Long listId, String newListName, Integer sessionToken) {
}
