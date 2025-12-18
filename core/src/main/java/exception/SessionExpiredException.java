package exception;

import protocol.ErrorType;

public class SessionExpiredException extends AuthenticationException {

    public SessionExpiredException(String message) {
        super(ErrorType.SESSION_EXPIRED, message);
    }
}
