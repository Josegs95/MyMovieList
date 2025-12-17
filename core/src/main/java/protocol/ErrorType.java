package protocol;

import exception.*;

import java.util.Map;

public enum ErrorType {

    BAD_REQUEST(400),
    AUTHENTICATION_FAILED(401),
    SESSION_EXPIRED(401),
    RESOURCE_NOT_FOUND(404),
    AUTHORIZATION_FAILED(403),
    INTERNAL_SERVER_ERROR(500);

    private final int statusCode;

    private static final Map<Class<? extends ServerException>, ErrorType> EXCEPTION_MAP = Map.of(
        ValidationException.class, BAD_REQUEST,
        AuthenticationException.class, AUTHENTICATION_FAILED,
        SessionExpiredException.class, SESSION_EXPIRED,
        ResourceNotFoundException.class, RESOURCE_NOT_FOUND,
        AuthorizationException.class, AUTHORIZATION_FAILED
    );

    ErrorType(int statusCode) {
        this.statusCode = statusCode;
    }

    public static ErrorType fromException(Class<? extends Exception> exceptionClass) {
        return EXCEPTION_MAP.getOrDefault(exceptionClass, INTERNAL_SERVER_ERROR);
    }

    public int getStatusCode() {
        return statusCode;
    }
}
