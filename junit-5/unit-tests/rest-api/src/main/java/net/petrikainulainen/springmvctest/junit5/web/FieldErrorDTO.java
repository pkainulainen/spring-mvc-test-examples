package net.petrikainulainen.springmvctest.junit5.web;

/**
 * This DTO contains the validation error that describe the
 * problem found from a JSON field.
 */
public class FieldErrorDTO {

    private final String field;
    private final String errorCode;

    public FieldErrorDTO(String field, String errorCode) {
        this.field = field;
        this.errorCode = errorCode;
    }

    public String getField() {
        return field;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
