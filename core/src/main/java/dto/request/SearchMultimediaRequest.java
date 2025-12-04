package dto.request;

public record SearchMultimediaRequest(Long userId, String searchText, Integer token) {}
