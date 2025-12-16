package exception;

public class SessionExpiredException extends ServerException {

    public SessionExpiredException(String message) {
        super(message);
    }
}
