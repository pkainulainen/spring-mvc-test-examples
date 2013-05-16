package net.petrikainulainen.spring.testmvc.todo.controller;

import net.petrikainulainen.spring.testmvc.todo.TodoTestUtil;
import net.petrikainulainen.spring.testmvc.todo.config.UnitTestContext;
import net.petrikainulainen.spring.testmvc.todo.dto.TodoDTO;
import net.petrikainulainen.spring.testmvc.todo.exception.TodoNotFoundException;
import net.petrikainulainen.spring.testmvc.todo.model.Todo;
import net.petrikainulainen.spring.testmvc.todo.service.TodoService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
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

    private MessageSource messageSourceMock;

    private TodoService serviceMock;

    @Resource
    private Validator validator;

    @Before
    public void setUp() {
        controller = new TodoController();

        messageSourceMock = mock(MessageSource.class);
        ReflectionTestUtils.setField(controller, "messageSource", messageSourceMock);

        serviceMock = mock(TodoService.class);
        ReflectionTestUtils.setField(controller, "service", serviceMock);
    }

    @Test
    public void showAddTodoForm() {
        BindingAwareModelMap model = new BindingAwareModelMap();

        String view = controller.showAddTodoForm(model);

        verifyZeroInteractions(messageSourceMock, serviceMock);
        assertEquals(TodoController.VIEW_TODO_ADD, view);

        TodoDTO formObject = (TodoDTO) model.asMap().get(TodoController.MODEL_ATTRIBUTE_TODO);

        assertNull(formObject.getId());
        assertNull(formObject.getDescription());
        assertNull(formObject.getTitle());
    }

    @Test
    public void add() {
        TodoDTO formObject = TodoTestUtil.createFormObject(null, TodoTestUtil.DESCRIPTION, TodoTestUtil.TITLE);

        Todo model = TodoTestUtil.createModel(TodoTestUtil.ID, TodoTestUtil.DESCRIPTION, TodoTestUtil.TITLE);
        when(serviceMock.add(formObject)).thenReturn(model);

        MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/todo/add");
        BindingResult result = bindAndValidate(mockRequest, formObject);

        RedirectAttributesModelMap attributes = new RedirectAttributesModelMap();

        initMessageSourceForFeedbackMessage(TodoController.FEEDBACK_MESSAGE_KEY_TODO_ADDED);

        String view = controller.add(formObject, result, attributes);

        verify(serviceMock, times(1)).add(formObject);
        verifyNoMoreInteractions(serviceMock);

        String expectedView = TodoTestUtil.createRedirectViewPath(TodoController.REQUEST_MAPPING_TODO_VIEW);
        assertEquals(expectedView, view);

        assertEquals(Long.valueOf((String) attributes.get(TodoController.PARAMETER_TODO_ID)), model.getId());

        assertFeedbackMessage(attributes, TodoController.FEEDBACK_MESSAGE_KEY_TODO_ADDED);
    }

    @Test
    public void addEmptyTodo() {
        TodoDTO formObject = TodoTestUtil.createFormObject(null, "", "");

        MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/todo/add");
        BindingResult result = bindAndValidate(mockRequest, formObject);

        RedirectAttributesModelMap attributes = new RedirectAttributesModelMap();

        String view = controller.add(formObject, result, attributes);

        verifyZeroInteractions(serviceMock, messageSourceMock);

        assertEquals(TodoController.VIEW_TODO_ADD, view);
        assertFieldErrors(result, FIELD_TITLE);
    }

    @Test
    public void addToDoWithTooLongDescriptionAndTitle() {
        String description = TodoTestUtil.createStringWithLength(Todo.MAX_LENGTH_DESCRIPTION + 1);
        String title = TodoTestUtil.createStringWithLength(Todo.MAX_LENGTH_TITLE + 1);

        TodoDTO formObject = TodoTestUtil.createFormObject(null, description, title);

        MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/todo/add");
        BindingResult result = bindAndValidate(mockRequest, formObject);

        RedirectAttributesModelMap attributes = new RedirectAttributesModelMap();

        String view = controller.add(formObject, result, attributes);

        verifyZeroInteractions(serviceMock, messageSourceMock);

        assertEquals(TodoController.VIEW_TODO_ADD, view);
        assertFieldErrors(result, FIELD_DESCRIPTION, FIELD_TITLE);
    }

    @Test
    public void deleteById() throws TodoNotFoundException {
        RedirectAttributesModelMap attributes = new RedirectAttributesModelMap();

        Todo model = TodoTestUtil.createModel(TodoTestUtil.ID, TodoTestUtil.DESCRIPTION, TodoTestUtil.TITLE);
        when(serviceMock.deleteById(TodoTestUtil.ID)).thenReturn(model);

        initMessageSourceForFeedbackMessage(TodoController.FEEDBACK_MESSAGE_KEY_TODO_DELETED);

        String view = controller.deleteById(TodoTestUtil.ID, attributes);

        verify(serviceMock, times(1)).deleteById(TodoTestUtil.ID);
        verifyNoMoreInteractions(serviceMock);

        assertFeedbackMessage(attributes, TodoController.FEEDBACK_MESSAGE_KEY_TODO_DELETED);

        String expectedView = TodoTestUtil.createRedirectViewPath(TodoController.REQUEST_MAPPING_TODO_LIST);
        assertEquals(expectedView, view);
    }

    @Test(expected = TodoNotFoundException.class)
    public void deleteByIdWhenToDoIsNotFound() throws TodoNotFoundException {
        RedirectAttributesModelMap attributes = new RedirectAttributesModelMap();

        when(serviceMock.deleteById(TodoTestUtil.ID)).thenThrow(new TodoNotFoundException(""));

        controller.deleteById(TodoTestUtil.ID, attributes);

        verify(serviceMock, times(1)).deleteById(TodoTestUtil.ID);
        verifyNoMoreInteractions(serviceMock);
        verifyZeroInteractions(messageSourceMock);
    }

    @Test
    public void findAll() {
        BindingAwareModelMap model = new BindingAwareModelMap();

        List<Todo> models = new ArrayList<Todo>();
        when(serviceMock.findAll()).thenReturn(models);

        String view = controller.findAll(model);

        verify(serviceMock, times(1)).findAll();
        verifyNoMoreInteractions(serviceMock);
        verifyZeroInteractions(messageSourceMock);

        assertEquals(TodoController.VIEW_TODO_LIST, view);
        assertEquals(models, model.asMap().get(TodoController.MODEL_ATTRIBUTE_TODO_LIST));
    }

    @Test
    public void findById() throws TodoNotFoundException {
        BindingAwareModelMap model = new BindingAwareModelMap();

        Todo found = TodoTestUtil.createModel(TodoTestUtil.ID, TodoTestUtil.DESCRIPTION, TodoTestUtil.TITLE);
        when(serviceMock.findById(TodoTestUtil.ID)).thenReturn(found);

        String view = controller.findById(TodoTestUtil.ID, model);

        verify(serviceMock, times(1)).findById(TodoTestUtil.ID);
        verifyNoMoreInteractions(serviceMock);
        verifyZeroInteractions(messageSourceMock);

        assertEquals(TodoController.VIEW_TODO_VIEW, view);
        assertEquals(found, model.asMap().get(TodoController.MODEL_ATTRIBUTE_TODO));
    }

    @Test(expected = TodoNotFoundException.class)
    public void findByIdWhenToDoIsNotFound() throws TodoNotFoundException {
        BindingAwareModelMap model = new BindingAwareModelMap();

        when(serviceMock.findById(TodoTestUtil.ID)).thenThrow(new TodoNotFoundException(""));

        controller.findById(TodoTestUtil.ID, model);

        verify(serviceMock, times(1)).findById(TodoTestUtil.ID);
        verifyNoMoreInteractions(serviceMock);
        verifyZeroInteractions(messageSourceMock);
    }

    @Test
    public void showUpdateTodoForm() throws TodoNotFoundException {
        BindingAwareModelMap model = new BindingAwareModelMap();

        Todo updated = TodoTestUtil.createModel(TodoTestUtil.ID, TodoTestUtil.DESCRIPTION, TodoTestUtil.TITLE);
        when(serviceMock.findById(TodoTestUtil.ID)).thenReturn(updated);

        String view = controller.showUpdateTodoForm(TodoTestUtil.ID, model);

        verify(serviceMock, times(1)).findById(TodoTestUtil.ID);
        verifyNoMoreInteractions(serviceMock);
        verifyZeroInteractions(messageSourceMock);

        assertEquals(TodoController.VIEW_TODO_UPDATE, view);

        TodoDTO formObject = (TodoDTO) model.asMap().get(TodoController.MODEL_ATTRIBUTE_TODO);

        assertEquals(updated.getId(), formObject.getId());
        assertEquals(updated.getDescription(), formObject.getDescription());
        assertEquals(updated.getTitle(), formObject.getTitle());
    }

    @Test(expected = TodoNotFoundException.class)
    public void showUpdateTodoFormWhenToDoIsNotFound() throws TodoNotFoundException {
        BindingAwareModelMap model = new BindingAwareModelMap();

        when(serviceMock.findById(TodoTestUtil.ID)).thenThrow(new TodoNotFoundException(""));

        controller.showUpdateTodoForm(TodoTestUtil.ID, model);

        verify(serviceMock, times(1)).findById(TodoTestUtil.ID);
        verifyNoMoreInteractions(serviceMock);
        verifyZeroInteractions(messageSourceMock);
    }

    @Test
    public void update() throws TodoNotFoundException {
        TodoDTO formObject = TodoTestUtil.createFormObject(TodoTestUtil.ID, TodoTestUtil.DESCRIPTION_UPDATED, TodoTestUtil.TITLE_UPDATED);

        Todo model = TodoTestUtil.createModel(TodoTestUtil.ID, TodoTestUtil.DESCRIPTION_UPDATED, TodoTestUtil.TITLE_UPDATED);
        when(serviceMock.update(formObject)).thenReturn(model);

        MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/todo/add");
        BindingResult result = bindAndValidate(mockRequest, formObject);

        RedirectAttributesModelMap attributes = new RedirectAttributesModelMap();

        initMessageSourceForFeedbackMessage(TodoController.FEEDBACK_MESSAGE_KEY_TODO_UPDATED);

        String view = controller.update(formObject, result, attributes);

        verify(serviceMock, times(1)).update(formObject);
        verifyNoMoreInteractions(serviceMock);

        String expectedView = TodoTestUtil.createRedirectViewPath(TodoController.REQUEST_MAPPING_TODO_VIEW);
        assertEquals(expectedView, view);

        assertEquals(Long.valueOf((String) attributes.get(TodoController.PARAMETER_TODO_ID)), model.getId());

        assertFeedbackMessage(attributes, TodoController.FEEDBACK_MESSAGE_KEY_TODO_UPDATED);
    }

    @Test
    public void updateEmptyToDo() throws TodoNotFoundException {
        TodoDTO formObject = TodoTestUtil.createFormObject(TodoTestUtil.ID, "", "");

        MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/todo/add");
        BindingResult result = bindAndValidate(mockRequest, formObject);

        RedirectAttributesModelMap attributes = new RedirectAttributesModelMap();

        String view = controller.update(formObject, result, attributes);

        verifyZeroInteractions(messageSourceMock, serviceMock);

        assertEquals(TodoController.VIEW_TODO_UPDATE, view);
        assertFieldErrors(result, FIELD_TITLE);
    }

    @Test
    public void updateToDoWhenDescriptionAndTitleAreTooLong() throws TodoNotFoundException {
        String description = TodoTestUtil.createStringWithLength(Todo.MAX_LENGTH_DESCRIPTION + 1);
        String title = TodoTestUtil.createStringWithLength(Todo.MAX_LENGTH_TITLE + 1);

        TodoDTO formObject = TodoTestUtil.createFormObject(TodoTestUtil.ID, description, title);

        MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/todo/add");
        BindingResult result = bindAndValidate(mockRequest, formObject);

        RedirectAttributesModelMap attributes = new RedirectAttributesModelMap();

        String view = controller.update(formObject, result, attributes);

        verifyZeroInteractions(messageSourceMock, serviceMock);

        assertEquals(TodoController.VIEW_TODO_UPDATE, view);
        assertFieldErrors(result, FIELD_DESCRIPTION, FIELD_TITLE);
    }

    @Test(expected = TodoNotFoundException.class)
    public void updateWhenToDoIsNotFound() throws TodoNotFoundException {
        TodoDTO formObject = TodoTestUtil.createFormObject(TodoTestUtil.ID, TodoTestUtil.DESCRIPTION_UPDATED, TodoTestUtil.TITLE_UPDATED);

        when(serviceMock.update(formObject)).thenThrow(new TodoNotFoundException(""));

        MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/todo/add");
        BindingResult result = bindAndValidate(mockRequest, formObject);

        RedirectAttributesModelMap attributes = new RedirectAttributesModelMap();

        controller.update(formObject, result, attributes);

        verify(serviceMock, times(1)).update(formObject);
        verifyNoMoreInteractions(serviceMock);
        verifyZeroInteractions(messageSourceMock);
    }

    private void assertFeedbackMessage(RedirectAttributes attributes, String messageCode) {
        assertFlashMessages(attributes, messageCode, TodoController.FLASH_MESSAGE_KEY_FEEDBACK);
    }

    private void assertFieldErrors(BindingResult result, String... fieldNames) {
        assertEquals(fieldNames.length, result.getFieldErrorCount());
        for (String fieldName : fieldNames) {
            assertNotNull(result.getFieldError(fieldName));
        }
    }

    private void assertFlashMessages(RedirectAttributes attributes, String messageCode, String flashMessageParameterName) {
        Map<String, ?> flashMessages = attributes.getFlashAttributes();
        Object message = flashMessages.get(flashMessageParameterName);

        assertNotNull(message);
        flashMessages.remove(message);
        assertTrue(flashMessages.isEmpty());

        verify(messageSourceMock, times(1)).getMessage(eq(messageCode), any(Object[].class), any(Locale.class));
        verifyNoMoreInteractions(messageSourceMock);
    }

    private BindingResult bindAndValidate(HttpServletRequest request, Object formObject) {
        WebDataBinder binder = new WebDataBinder(formObject);
        binder.setValidator(validator);
        binder.bind(new MutablePropertyValues(request.getParameterMap()));
        binder.getValidator().validate(binder.getTarget(), binder.getBindingResult());
        return binder.getBindingResult();
    }

    private void initMessageSourceForFeedbackMessage(String feedbackMessageCode) {
        when(messageSourceMock.getMessage(eq(feedbackMessageCode), any(Object[].class), any(Locale.class))).thenReturn(FEEDBACK_MESSAGE);
    }
}
