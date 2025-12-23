package exception;

import protocol.ErrorType;

public class SessionNotFoundException extends AuthenticationException {

    public SessionNotFoundException(String message) {
        super(ErrorType.SESSION_NOT_FOUND, message);
    }
}
