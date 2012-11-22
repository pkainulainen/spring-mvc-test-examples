package net.petrikainulainen.spring.testmvc.todo.controller;

import net.petrikainulainen.spring.testmvc.common.util.LocaleContextHolderWrapper;
import net.petrikainulainen.spring.testmvc.todo.dto.FormValidationErrorDTO;
import net.petrikainulainen.spring.testmvc.todo.dto.TodoDTO;
import net.petrikainulainen.spring.testmvc.todo.exception.FormValidationError;
import net.petrikainulainen.spring.testmvc.todo.exception.TodoNotFoundException;
import net.petrikainulainen.spring.testmvc.todo.model.Todo;
import net.petrikainulainen.spring.testmvc.todo.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Petri Kainulainen
 */
@Controller
public class TodoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TodoController.class);

    protected static final String ERROR_MESSAGE_CODE_VALIDATION_ERROR = "error.message.validation.error";

    protected static final String OBJECT_NAME_TODO = "todo";

    @Resource
    private TodoService service;

    @Resource
    private LocaleContextHolderWrapper localeHolderWrapper;

    @Resource
    private MessageSource messageSource;

    @Resource
    private Validator validator;


    @RequestMapping(value = "/todo", method = RequestMethod.POST)
    @ResponseBody
    public TodoDTO add(@RequestBody TodoDTO dto) throws FormValidationError {
        LOGGER.debug("Adding a new to-do entry with information: {}", dto);

        validate(OBJECT_NAME_TODO, dto);

        Todo added = service.add(dto);
        LOGGER.debug("Added a to-do entry with information: {}", added);

       return createDTO(added);
    }

    @RequestMapping(value = "/todo/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public TodoDTO deleteById(@PathVariable("id") Long id) throws TodoNotFoundException {
        LOGGER.debug("Deleting a to-do entry with id: {}", id);

        Todo deleted = service.deleteById(id);
        LOGGER.debug("Deleted to-do entry with information: {}", deleted);

        return createDTO(deleted);
    }

    @RequestMapping(value = "/todo", method = RequestMethod.GET)
    @ResponseBody
    public List<TodoDTO> findAll() {
        LOGGER.debug("Finding all todo entries.");

        List<Todo> models = service.findAll();
        LOGGER.debug("Found {} to-do entries.", models.size());

        return createDTOs(models);
    }

    private List<TodoDTO> createDTOs(List<Todo> models) {
        List<TodoDTO> dtos = new ArrayList<TodoDTO>();

        for (Todo model: models) {
            dtos.add(createDTO(model));
        }

        return dtos;
    }

    @RequestMapping(value = "/todo/{id}", method = RequestMethod.GET)
    @ResponseBody
    public TodoDTO findById(@PathVariable("id") Long id) throws TodoNotFoundException {
        LOGGER.debug("Finding to-do entry with id: {}", id);

        Todo found = service.findById(id);
        LOGGER.debug("Found to-do entry with information: {}", found);

        return createDTO(found);
    }

    @RequestMapping(value = "/todo/{id}", method = RequestMethod.POST)
    @ResponseBody
    public TodoDTO update(@RequestBody TodoDTO dto, @PathVariable("id") Long todoId) throws TodoNotFoundException, FormValidationError {
        LOGGER.debug("Updating a to-do entry with information: {}", dto);

        validate(OBJECT_NAME_TODO, dto);

        Todo updated = service.update(dto);
        LOGGER.debug("Updated the information of a to-entry to: {}", updated);

        return createDTO(updated);
    }

    private TodoDTO createDTO(Todo model) {
        TodoDTO dto = new TodoDTO();

        dto.setId(model.getId());
        dto.setDescription(model.getDescription());
        dto.setTitle(model.getTitle());

        return dto;
    }

    private void validate(String objectName, Object validated) throws FormValidationError {
        LOGGER.debug("Validating object: " + validated);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(validated, objectName);
        validator.validate(validated, bindingResult);

        if (bindingResult.hasErrors()) {
            LOGGER.debug("Validation errors found:" + bindingResult.getFieldErrors());
            throw new FormValidationError(bindingResult.getFieldErrors());
        }
    }

    @ExceptionHandler(FormValidationError.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public FormValidationErrorDTO handleFormValidationError(FormValidationError validationError) {
        LOGGER.debug("Handling form validation error");

        Locale current = localeHolderWrapper.getCurrentLocale();

        List<FieldError> fieldErrors = validationError.getFieldErrors();

        FormValidationErrorDTO dto = new FormValidationErrorDTO();

        for (FieldError fieldError: fieldErrors) {
            String[] fieldErrorCodes = fieldError.getCodes();
            for (String fieldErrorCode: fieldErrorCodes) {
                String localizedError = messageSource.getMessage(fieldErrorCode, fieldError.getArguments(), current);
                if (localizedError != null && !localizedError.equals(fieldErrorCode)) {
                    LOGGER.debug("Adding error message: {} to field: {}", localizedError, fieldError.getField());
                    dto.addFieldError(fieldError.getField(), localizedError);
                    break;
                }
            }
        }

        return dto;
    }

    @ExceptionHandler(TodoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleTodoNotFoundException(TodoNotFoundException ex) {
        LOGGER.debug("handling 404 error on a todo entry");
    }
}
