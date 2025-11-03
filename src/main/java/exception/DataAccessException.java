package exception;

public class DataAccessException extends RuntimeException {
    public DataAccessException(String msg, Throwable cause) { super(msg, cause); }
}
