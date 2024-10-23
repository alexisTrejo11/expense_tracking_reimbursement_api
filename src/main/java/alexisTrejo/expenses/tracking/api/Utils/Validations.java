package alexisTrejo.expenses.tracking.api.Utils;

import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.stream.Collectors;

public class Validations {

    public static Result<Void> validateDTO(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Collect validation error messages
            List<String> errorMessages = bindingResult.getFieldErrors()
                    .stream()
                    .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                    .toList();

            return Result.error(errorMessages.toString());
        }

        return Result.success();
    }
}

