package net.petrikainulainen.spring.testmvc.todo.dto;

import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * @author Petri Kainulainen
 */
public class ValidationErrorDTOTest {

    private static final String FIELD_PATH = "foo";
    private static final String ERROR_MESSAGE = "bar";

    @Test
    public void addFieldError() {
        ValidationErrorDTO dto = new ValidationErrorDTO();

        dto.addFieldError(FIELD_PATH, ERROR_MESSAGE);

        List<FieldErrorDTO> fieldErrors = dto.getFieldErrors();

        assertEquals(1, fieldErrors.size());

        FieldErrorDTO fieldError = fieldErrors.get(0);
        assertEquals(FIELD_PATH, fieldError.getPath());
        assertEquals(ERROR_MESSAGE, fieldError.getMessage());
    }
}
