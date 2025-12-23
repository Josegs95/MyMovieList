package exception;

import protocol.ErrorType;

public class UserNotFoundException extends ResourceNotFoundException {

    public UserNotFoundException(String message) {
        super(ErrorType.USER_NOT_FOUND, message);
    }
}
