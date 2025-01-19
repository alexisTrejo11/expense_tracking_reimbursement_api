package alexisTrejo.expenses.tracking.api.Utils;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class CustomGlobalExceptionHandler {

    /* Validation Data Exceptions */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        String errorMessage = String.join(", ", errors);
        StringBuilder validation = new StringBuilder("Validation Error");

        // Adjust the message based on the number of errors
        if (errors.size() > 1) {
            validation.append("s: "); // For multiple errors
        } else {
            validation.append(": "); // For a single error
        }

        // Complete the message with the actual error details
        validation.append(errorMessage);

        return ResponseEntity.badRequest().body(validation);
    }

    /* To Many Request Exception
    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<String> handleRequestNotPermitted(RequestNotPermitted exception) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body("Rate limit exceeded. Please try again later.");
    }
    */

    /* Not Found Exception */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> entityNotFound(EntityNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // SQL Conflict
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return new ResponseEntity<>("Unique constraint violation: " + Objects.requireNonNull(ex.getRootCause()).getMessage(), HttpStatus.CONFLICT);
    }

    /* Generic Exception */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return new ResponseEntity<>( "An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
