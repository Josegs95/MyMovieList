package protocol;

import exception.*;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public enum ErrorType {

    BAD_REQUEST(400),
    UNEXPECTED_MESSAGE(400),
    MALFORMED_REQUEST(400),
    AUTHENTICATION_FAILED(401),
    INVALID_CREDENTIALS(401),
    SESSION_EXPIRED(401),
    SESSION_NOT_FOUND(401),
    AUTHORIZATION_FAILED(403),
    LIST_NOT_BELONG_USER(403),
    RESOURCE_NOT_FOUND(404),
    USER_NOT_FOUND(404),
    USER_LIST_NOT_FOUND(404),
    MULTIMEDIA_NOT_FOUND_IN_LIST(404),
    USERNAME_ALREADY_EXISTS(409),
    LIST_NAME_ALREADY_EXISTS_FOR_USER(409),
    MULTIMEDIA_ALREADY_IN_LIST(409),
    INTERNAL_SERVER_ERROR(500),
    API_RELATED_ERROR(502);

    private final int statusCode;

    private static final Map<Class<? extends ServerException>, ErrorType> EXCEPTION_MAP = new HashMap<>();

    static {
        EXCEPTION_MAP.put(UnexpectedMessageException.class, UNEXPECTED_MESSAGE);
        EXCEPTION_MAP.put(ValidationException.class, MALFORMED_REQUEST);
        EXCEPTION_MAP.put(InvalidCredentialsException.class, INVALID_CREDENTIALS);
        EXCEPTION_MAP.put(SessionExpiredException.class, SESSION_EXPIRED);
        EXCEPTION_MAP.put(SessionNotFoundException.class, SESSION_NOT_FOUND);
        EXCEPTION_MAP.put(ListDoesNotBelongToUserException.class, LIST_NOT_BELONG_USER);
        EXCEPTION_MAP.put(UserNotFoundException.class, USER_NOT_FOUND);
        EXCEPTION_MAP.put(UserListNotFoundException.class, USER_LIST_NOT_FOUND);
        EXCEPTION_MAP.put(MultimediaNotFoundInListException.class, MULTIMEDIA_NOT_FOUND_IN_LIST);
        EXCEPTION_MAP.put(UsernameAlreadyExistsException.class, USERNAME_ALREADY_EXISTS);
        EXCEPTION_MAP.put(ListNameAlreadyExistsForUserException.class, LIST_NAME_ALREADY_EXISTS_FOR_USER);
        EXCEPTION_MAP.put(MultimediaAlreadyExistsInListException.class, MULTIMEDIA_ALREADY_IN_LIST);
        EXCEPTION_MAP.put(ExternalServiceException.class, API_RELATED_ERROR);
    }

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
