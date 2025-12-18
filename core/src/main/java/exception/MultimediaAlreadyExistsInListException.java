package exception;

import protocol.ErrorType;

public class MultimediaAlreadyExistsInListException extends ConflictException {

    public MultimediaAlreadyExistsInListException(String message) {
        super(ErrorType.MULTIMEDIA_ALREADY_IN_LIST, message);
    }
}
