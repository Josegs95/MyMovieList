package exception;

import protocol.ErrorType;

public class AuthenticationException extends ServerException{

    public AuthenticationException(ErrorType errorType, String message) {
        super(errorType, message);
    }
}
