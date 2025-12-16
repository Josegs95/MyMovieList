package protocol.dto.request;

public record SearchMultimediaRequest(Long userId, String searchText, String sessionToken) {}
