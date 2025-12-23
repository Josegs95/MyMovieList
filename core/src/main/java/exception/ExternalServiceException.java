package exception;

import protocol.ErrorType;

public class ExternalServiceException extends ServerException {

    public ExternalServiceException(String message) {
        super(ErrorType.API_RELATED_ERROR, message);
    }
}
