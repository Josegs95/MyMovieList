package dto.request;

public record CreateListRequest(Long userId, String listName, Integer token) {
}
