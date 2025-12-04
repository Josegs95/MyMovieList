package dto.request;

import entity.MultimediaType;

public record MultimediaDetailRequest(Long userId, String apiId, MultimediaType type, Integer token) {}
