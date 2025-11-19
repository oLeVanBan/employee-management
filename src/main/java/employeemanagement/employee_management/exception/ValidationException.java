package employeemanagement.employee_management.exception;

import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Custom exception representing Bean Validation failures collected from BindingResult.
 */
public class ValidationException extends RuntimeException {

    private final List<String> errors;

    public ValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors == null ? Collections.emptyList() : errors;
    }

    public List<String> getErrors() {
        return errors;
    }

    public static ValidationException fromBindingResult(BindingResult bindingResult) {
        List<String> messages = bindingResult.getFieldErrors().stream()
                .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        return new ValidationException("Validation failed", messages);
    }
}
