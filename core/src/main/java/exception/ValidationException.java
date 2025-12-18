package exception;

import protocol.ErrorType;

public class ValidationException extends ServerException{

    public ValidationException(String message) {
        super(ErrorType.MALFORMED_REQUEST, message);
    }
}
