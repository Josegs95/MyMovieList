package exception;

public class ResourceNotFoundException extends ServerException{

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
