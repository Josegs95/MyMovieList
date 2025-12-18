package exception;

import protocol.ErrorType;

public class ListNameAlreadyExistsForUserException extends ConflictException {

    public ListNameAlreadyExistsForUserException(String message) {
        super(ErrorType.LIST_NAME_ALREADY_EXISTS_FOR_USER, message);
    }
}
