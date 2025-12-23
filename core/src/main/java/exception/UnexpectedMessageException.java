package exception;

import protocol.ErrorType;

public class UnexpectedMessageException extends ServerException {

    public UnexpectedMessageException(String message) {
        super(ErrorType.UNEXPECTED_MESSAGE, message);
    }
}
