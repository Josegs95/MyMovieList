package exception;

import protocol.ErrorType;

public class InvalidCredentialsException extends AuthenticationException {

    public InvalidCredentialsException(String message) {
        super(ErrorType.INVALID_CREDENTIALS, message);
    }
}
