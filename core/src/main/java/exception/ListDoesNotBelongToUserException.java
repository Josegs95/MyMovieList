package exception;

import protocol.ErrorType;

public class ListDoesNotBelongToUserException extends AuthorizationException {

    public ListDoesNotBelongToUserException(String message) {
        super(ErrorType.LIST_NOT_BELONG_USER, message);
    }
}
