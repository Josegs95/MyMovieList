package exception;

import protocol.ErrorType;

public class ResourceNotFoundException extends ServerException{

    public ResourceNotFoundException(ErrorType errorType, String message) {
        super(errorType, message);
    }
}
