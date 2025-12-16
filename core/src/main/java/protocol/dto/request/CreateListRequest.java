package protocol.dto.request;

public record CreateListRequest(Long userId, String listName, String sessionToken) {
}
