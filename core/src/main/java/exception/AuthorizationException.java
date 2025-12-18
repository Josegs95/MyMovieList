package exception;

import protocol.ErrorType;

public class AuthorizationException extends ServerException{

    public AuthorizationException(ErrorType errorType, String message) {
        super(errorType, message);
    }
}
