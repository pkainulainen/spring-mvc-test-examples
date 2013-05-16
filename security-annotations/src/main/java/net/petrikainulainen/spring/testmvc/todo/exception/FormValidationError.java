package net.petrikainulainen.spring.testmvc.todo.exception;

import org.springframework.validation.FieldError;

import java.util.List;

/**
 * @author Petri Kainulainen
 */
public class FormValidationError extends Exception {

    private List<FieldError> fieldErrors;

    public FormValidationError(List<FieldError> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }
}
