package exception;

public class DatabaseException extends Exception{

    // Error codes: 23 (Duplicity in UNIQUE field), -1 (Unknown error)
    private int errorCode;

    public DatabaseException(String message) {
        super(message);
        this.errorCode = -1;
    }

    public DatabaseException(int errorCode, String message) {
        this(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
