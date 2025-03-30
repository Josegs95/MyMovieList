package exception;

public class DatabaseException extends Exception{

    // Error codes: 23(Duplicity in UNIQUE field)
    private int errorCode;

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
