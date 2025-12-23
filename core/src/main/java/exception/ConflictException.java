package exception;

import protocol.ErrorType;

public class ConflictException extends ServerException{

    public ConflictException(ErrorType errorType, String message) {
        super(errorType, message);
    }
}
