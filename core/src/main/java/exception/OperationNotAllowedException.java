package exception;

public class OperationNotAllowedException extends ServerException{

    public OperationNotAllowedException(String message) {
        super(message);
    }
}
