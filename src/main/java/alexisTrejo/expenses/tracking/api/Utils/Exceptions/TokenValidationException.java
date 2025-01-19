package alexisTrejo.expenses.tracking.api.Utils.Exceptions;

public class TokenValidationException extends RuntimeException {
    public TokenValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenValidationException(String message) {
        super(message);
    }
}
