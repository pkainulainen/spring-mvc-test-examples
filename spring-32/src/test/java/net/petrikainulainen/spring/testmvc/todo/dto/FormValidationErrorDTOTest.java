package net.petrikainulainen.spring.testmvc.todo.dto;

import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * @author Petri Kainulainen
 */
public class FormValidationErrorDTOTest {

    private static final String FIELD_PATH = "foo";
    private static final String ERROR_MESSAGE = "bar";

    @Test
    public void addFieldError() {
        FormValidationErrorDTO dto = new FormValidationErrorDTO();

        dto.addFieldError(FIELD_PATH, ERROR_MESSAGE);

        List<FieldValidationErrorDTO> fieldErrors = dto.getFieldErrors();

        assertEquals(1, fieldErrors.size());

        FieldValidationErrorDTO fieldError = fieldErrors.get(0);
        assertEquals(FIELD_PATH, fieldError.getPath());
        assertEquals(ERROR_MESSAGE, fieldError.getMessage());
    }
}
