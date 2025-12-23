package protocol.dto.request;

import model.entity.MultimediaType;
import protocol.AuthCredentials;

public record MultimediaDetailRequest(AuthCredentials auth, String apiId, MultimediaType type) {}
