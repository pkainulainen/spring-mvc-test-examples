package net.petrikainulainen.spring.testmvc.todo.controller;

import net.petrikainulainen.spring.testmvc.common.util.LocaleContextHolderWrapper;
import net.petrikainulainen.spring.testmvc.todo.TodoTestUtil;
import net.petrikainulainen.spring.testmvc.todo.config.UnitTestContext;
import net.petrikainulainen.spring.testmvc.todo.dto.TodoDTO;
import net.petrikainulainen.spring.testmvc.todo.exception.FormValidationError;
import net.petrikainulainen.spring.testmvc.todo.exception.TodoNotFoundException;
import net.petrikainulainen.spring.testmvc.todo.model.Todo;
import net.petrikainulainen.spring.testmvc.todo.service.TodoService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.Validator;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Petri Kainulainen
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {UnitTestContext.class})
public class TodoControllerTest {

    private static final String FEEDBACK_MESSAGE = "feedbackMessage";
    private static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_TITLE = "title";

    private TodoController controller;

    private LocaleContextHolderWrapper localeHolderWrapperMock;

    private MessageSource messageSourceMock;

    private TodoService serviceMock;

    @Resource
    private Validator validator;

    @Before
    public void setUp() {
        controller = new TodoController();

        localeHolderWrapperMock = mock(LocaleContextHolderWrapper.class);
        ReflectionTestUtils.setField(controller, "localeHolderWrapper", localeHolderWrapperMock);

        messageSourceMock = mock(MessageSource.class);
        ReflectionTestUtils.setField(controller, "messageSource", messageSourceMock);

        serviceMock = mock(TodoService.class);
        ReflectionTestUtils.setField(controller, "service", serviceMock);

        ReflectionTestUtils.setField(controller, "validator", validator);
    }

    @Test
    public void add() throws FormValidationError {
        TodoDTO dto = TodoTestUtil.createDTO(null, TodoTestUtil.DESCRIPTION, TodoTestUtil.TITLE);
        Todo expected = TodoTestUtil.createModel(TodoTestUtil.ID, TodoTestUtil.DESCRIPTION, TodoTestUtil.TITLE);
        when(serviceMock.add(dto)).thenReturn(expected);

        TodoDTO actual = controller.add(dto);

        verify(serviceMock, times(1)).add(dto);
        verifyNoMoreInteractions(serviceMock);
        verifyZeroInteractions(localeHolderWrapperMock, messageSourceMock);

        assertTodo(expected, actual);
    }

    @Test(expected = FormValidationError.class)
    public void addEmptyTodo() throws FormValidationError {
        TodoDTO dto = TodoTestUtil.createDTO(null, "", "");

        controller.add(dto);

        verifyZeroInteractions(localeHolderWrapperMock, messageSourceMock, serviceMock);
    }

    @Test(expected = FormValidationError.class)
    public void addTodoWhenTitleAndDescriptionAreTooLong() throws FormValidationError {
        String description = TodoTestUtil.createStringWithLength(Todo.MAX_LENGTH_DESCRIPTION + 1);
        String title = TodoTestUtil.createStringWithLength(Todo.MAX_LENGTH_TITLE + 1);

        TodoDTO dto = TodoTestUtil.createDTO(null, description, title);

        controller.add(dto);

        verifyZeroInteractions(localeHolderWrapperMock, messageSourceMock, serviceMock);
    }

    @Test
    public void deleteById() throws TodoNotFoundException {
        Todo expected = TodoTestUtil.createModel(TodoTestUtil.ID, TodoTestUtil.DESCRIPTION, TodoTestUtil.TITLE);
        when(serviceMock.deleteById(TodoTestUtil.ID)).thenReturn(expected);

        TodoDTO actual = controller.deleteById(TodoTestUtil.ID);

        verify(serviceMock, times(1)).deleteById(TodoTestUtil.ID);
        verifyNoMoreInteractions(serviceMock);
        verifyZeroInteractions(localeHolderWrapperMock, messageSourceMock);

        assertTodo(expected, actual);
    }

    @Test(expected = TodoNotFoundException.class)
    public void deleteByIdWhenTodoIsNotFound() throws TodoNotFoundException {
        when(serviceMock.deleteById(TodoTestUtil.ID)).thenThrow(new TodoNotFoundException(""));

        controller.deleteById(TodoTestUtil.ID);

        verify(serviceMock, times(1)).deleteById(TodoTestUtil.ID);
        verifyNoMoreInteractions(serviceMock);
        verifyZeroInteractions(localeHolderWrapperMock, messageSourceMock);
    }

    @Test
    public void findAll() {
        Todo model = TodoTestUtil.createModel(TodoTestUtil.ID, TodoTestUtil.DESCRIPTION, TodoTestUtil.TITLE);
        List<Todo> expected = createModels(model);

        when(serviceMock.findAll()).thenReturn(expected);

        List<TodoDTO> actual = controller.findAll();

        verify(serviceMock, times(1)).findAll();
        verifyNoMoreInteractions(serviceMock);
        verifyZeroInteractions(localeHolderWrapperMock, messageSourceMock);

        assertTodos(expected, actual);
    }

    @Test
    public void update() throws FormValidationError, TodoNotFoundException {
        TodoDTO dto = TodoTestUtil.createDTO(TodoTestUtil.ID, TodoTestUtil.DESCRIPTION_UPDATED, TodoTestUtil.TITLE_UPDATED);
        Todo expected = TodoTestUtil.createModel(TodoTestUtil.ID, TodoTestUtil.DESCRIPTION, TodoTestUtil.TITLE);
        when(serviceMock.update(dto)).thenReturn(expected);

        TodoDTO actual = controller.update(dto, TodoTestUtil.ID);

        verify(serviceMock, times(1)).update(dto);
        verifyNoMoreInteractions(serviceMock);
        verifyZeroInteractions(localeHolderWrapperMock, messageSourceMock);

        assertTodo(expected, actual);
    }

    @Test(expected = FormValidationError.class)
    public void updateEmptyTodo() throws FormValidationError, TodoNotFoundException {
        TodoDTO dto = TodoTestUtil.createDTO(TodoTestUtil.ID, "", "");

        controller.update(dto, TodoTestUtil.ID);

        verifyZeroInteractions(localeHolderWrapperMock, messageSourceMock, serviceMock);
    }

    @Test(expected = FormValidationError.class)
    public void updateTodoWhenTitleAndDescriptionAreTooLong() throws FormValidationError, TodoNotFoundException {
        String description = TodoTestUtil.createStringWithLength(Todo.MAX_LENGTH_DESCRIPTION + 1);
        String title = TodoTestUtil.createStringWithLength(Todo.MAX_LENGTH_TITLE + 1);

        TodoDTO dto = TodoTestUtil.createDTO(TodoTestUtil.ID, description, title);

        controller.update(dto, TodoTestUtil.ID);

        verifyZeroInteractions(localeHolderWrapperMock, messageSourceMock, serviceMock);
    }

    @Test(expected = TodoNotFoundException.class)
    public void updateWhenTodoIsNotFound() throws FormValidationError, TodoNotFoundException {
        TodoDTO dto = TodoTestUtil.createDTO(TodoTestUtil.ID, TodoTestUtil.DESCRIPTION, TodoTestUtil.TITLE);
        when(serviceMock.update(dto)).thenThrow(new TodoNotFoundException(""));

        controller.update(dto, TodoTestUtil.ID);

        verify(serviceMock, times(1)).update(dto);
        verifyNoMoreInteractions(serviceMock);
        verifyZeroInteractions(localeHolderWrapperMock, messageSourceMock);
    }

    private void assertTodos(List<Todo> expected, List<TodoDTO> actual) {
        assertEquals(expected.size(), actual.size());

        for (int index = 0; index < expected.size(); index++) {
            Todo model = expected.get(index);
            TodoDTO dto = actual.get(index);
            assertTodo(model, dto);
        }
    }

    private List<Todo> createModels(Todo... models) {
        List<Todo> todos = new ArrayList<Todo>();

        for (Todo model: models) {
            todos.add(model);
        }

        return todos;
    }

    @Test
    public void findById() throws TodoNotFoundException {
        Todo expected = TodoTestUtil.createModel(TodoTestUtil.ID, TodoTestUtil.DESCRIPTION, TodoTestUtil.TITLE);
        when(serviceMock.findById(TodoTestUtil.ID)).thenReturn(expected);

        TodoDTO actual = controller.findById(TodoTestUtil.ID);

        verify(serviceMock, times(1)).findById(TodoTestUtil.ID);
        verifyNoMoreInteractions(serviceMock);
        verifyZeroInteractions(localeHolderWrapperMock, messageSourceMock);

        assertTodo(expected, actual);
    }

    @Test(expected = TodoNotFoundException.class)
    public void findByIdWhenTodoIsNotFound() throws TodoNotFoundException {
        when(serviceMock.findById(TodoTestUtil.ID)).thenThrow(new TodoNotFoundException(""));

        controller.findById(TodoTestUtil.ID);

        verify(serviceMock, times(1)).findById(TodoTestUtil.ID);
        verifyNoMoreInteractions(serviceMock);
        verifyZeroInteractions(localeHolderWrapperMock, messageSourceMock);
    }

    private void assertTodo(Todo expected, TodoDTO actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getTitle(), actual.getTitle());
    }
}
