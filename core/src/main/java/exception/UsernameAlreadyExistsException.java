package exception;

import protocol.ErrorType;

public class UsernameAlreadyExistsException extends ConflictException {

    public UsernameAlreadyExistsException(String message) {
        super(ErrorType.USERNAME_ALREADY_EXISTS, message);
    }
}
