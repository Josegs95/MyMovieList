package protocol.dto.request;

import model.entity.MultimediaType;

public record MultimediaDetailRequest(Long userId, String apiId, MultimediaType type, String sessionToken) {}
