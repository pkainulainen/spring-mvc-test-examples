package net.petrikainulainen.springmvctest.junit5.web;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains the field errors that describe the validation
 * errors found from the incoming HTTP request.
 */
public class ValidationErrorDTO {

    private final List<FieldErrorDTO> fieldErrors = new ArrayList<>();

    public ValidationErrorDTO() {
    }

    public void addFieldError(String path, String message) {
        FieldErrorDTO error = new FieldErrorDTO(path, message);
        fieldErrors.add(error);
    }

    public List<FieldErrorDTO> getFieldErrors() {
        return fieldErrors;
    }
}
