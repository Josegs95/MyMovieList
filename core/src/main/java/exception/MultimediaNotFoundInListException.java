package exception;

import protocol.ErrorType;

public class MultimediaNotFoundInListException extends ResourceNotFoundException {

    public MultimediaNotFoundInListException(String message) {
        super(ErrorType.MULTIMEDIA_NOT_FOUND_IN_LIST, message);
    }
}
