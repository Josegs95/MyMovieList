package protocol.dto.request;

public record DeleteListRequest(Long userId, Long listId, Integer sessionToken) {
}
