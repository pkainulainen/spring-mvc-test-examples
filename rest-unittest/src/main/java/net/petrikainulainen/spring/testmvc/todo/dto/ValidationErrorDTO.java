package net.petrikainulainen.spring.testmvc.todo.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Petri Kainulainen
 */
public class ValidationErrorDTO {

    private List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    public ValidationErrorDTO() {

    }

    public void addFieldError(String path, String message) {
        FieldErrorDTO fieldError = new FieldErrorDTO(path, message);
        fieldErrors.add(fieldError);
    }

    public List<FieldErrorDTO> getFieldErrors() {
        return fieldErrors;
    }
}
