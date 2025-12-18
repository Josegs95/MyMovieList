package protocol.dto.request;

import protocol.AuthCredentials;

public record SearchMultimediaRequest(AuthCredentials auth, String searchText) {}
