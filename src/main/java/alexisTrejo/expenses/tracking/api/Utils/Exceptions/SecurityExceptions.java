package alexisTrejo.expenses.tracking.api.Utils.Exceptions;

public class SecurityExceptions {
    public static class JwtTokenMissingException extends RuntimeException {
        public JwtTokenMissingException() {
            super("Authorization token is required");
        }
    }

    public static class JwtTokenExpiredException extends RuntimeException {
        public JwtTokenExpiredException() {
            super("Authorization token has expired");
        }
    }

    public static class JwtTokenMalformedException extends RuntimeException {
        public JwtTokenMalformedException() {
            super("Invalid token format");
        }
    }

    public static class JwtTokenInvalidException extends RuntimeException {
        public JwtTokenInvalidException() {
            super("Invalid authorization token");
        }
    }
}