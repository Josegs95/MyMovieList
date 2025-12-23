package exception;

import protocol.ErrorType;

public class UserListNotFoundException extends ResourceNotFoundException {

    public UserListNotFoundException(String message) {
        super(ErrorType.USER_LIST_NOT_FOUND, message);
    }
}
