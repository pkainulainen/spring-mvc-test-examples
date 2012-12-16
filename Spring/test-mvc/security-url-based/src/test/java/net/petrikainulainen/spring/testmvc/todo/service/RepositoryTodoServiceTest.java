package net.petrikainulainen.spring.testmvc.todo.service;

import net.petrikainulainen.spring.testmvc.todo.TodoTestUtil;
import net.petrikainulainen.spring.testmvc.todo.dto.TodoDTO;
import net.petrikainulainen.spring.testmvc.todo.exception.TodoNotFoundException;
import net.petrikainulainen.spring.testmvc.todo.model.Todo;
import net.petrikainulainen.spring.testmvc.todo.repository.TodoRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * @author Petri Kainulainen
 */
public class RepositoryTodoServiceTest {

    private RepositoryTodoService service;

    private TodoRepository repositoryMock;

    @Before
    public void setUp() {
        service = new RepositoryTodoService();

        repositoryMock = mock(TodoRepository.class);
        ReflectionTestUtils.setField(service, "repository", repositoryMock);
    }

    @Test
    public void add() {
        TodoDTO dto = TodoTestUtil.createDTO(null, TodoTestUtil.DESCRIPTION, TodoTestUtil.TITLE);

        service.add(dto);

        ArgumentCaptor<Todo> toDoArgument = ArgumentCaptor.forClass(Todo.class);
        verify(repositoryMock, times(1)).save(toDoArgument.capture());
        verifyNoMoreInteractions(repositoryMock);

        Todo model = toDoArgument.getValue();

        assertNull(model.getId());
        assertEquals(dto.getDescription(), model.getDescription());
        assertEquals(dto.getTitle(), model.getTitle());
    }

    @Test
    public void deleteById() throws TodoNotFoundException {
        Todo model = TodoTestUtil.createModel(TodoTestUtil.ID, TodoTestUtil.DESCRIPTION, TodoTestUtil.TITLE);
        when(repositoryMock.findOne(TodoTestUtil.ID)).thenReturn(model);

        Todo actual = service.deleteById(TodoTestUtil.ID);

        verify(repositoryMock, times(1)).findOne(TodoTestUtil.ID);
        verify(repositoryMock, times(1)).delete(model);
        verifyNoMoreInteractions(repositoryMock);

        assertEquals(model, actual);
    }

    @Test(expected = TodoNotFoundException.class)
    public void deleteByIdWhenToDoIsNotFound() throws TodoNotFoundException {
        when(repositoryMock.findOne(TodoTestUtil.ID)).thenReturn(null);

        service.deleteById(TodoTestUtil.ID);

        verify(repositoryMock, times(1)).findOne(TodoTestUtil.ID);
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    public void findAll() {
        List<Todo> models = new ArrayList<Todo>();
        when(repositoryMock.findAll()).thenReturn(models);

        List<Todo> actual = service.findAll();

        verify(repositoryMock, times(1)).findAll();
        verifyNoMoreInteractions(repositoryMock);

        assertEquals(models, actual);
    }

    @Test
    public void findById() throws TodoNotFoundException {
        Todo model = TodoTestUtil.createModel(TodoTestUtil.ID, TodoTestUtil.DESCRIPTION, TodoTestUtil.TITLE);
        when(repositoryMock.findOne(TodoTestUtil.ID)).thenReturn(model);

        Todo actual = service.findById(TodoTestUtil.ID);

        verify(repositoryMock, times(1)).findOne(TodoTestUtil.ID);
        verifyNoMoreInteractions(repositoryMock);

        assertEquals(model, actual);
    }

    @Test(expected = TodoNotFoundException.class)
    public void findByIdWhenToDoIsNotFound() throws TodoNotFoundException {
        when(repositoryMock.findOne(TodoTestUtil.ID)).thenReturn(null);

        service.findById(TodoTestUtil.ID);

        verify(repositoryMock, times(1)).findOne(TodoTestUtil.ID);
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    public void update() throws TodoNotFoundException {
        TodoDTO dto = TodoTestUtil.createDTO(TodoTestUtil.ID, TodoTestUtil.DESCRIPTION_UPDATED, TodoTestUtil.TITLE_UPDATED);
        Todo model = TodoTestUtil.createModel(TodoTestUtil.ID, TodoTestUtil.DESCRIPTION, TodoTestUtil.TITLE);
        when(repositoryMock.findOne(dto.getId())).thenReturn(model);

        Todo actual = service.update(dto);

        verify(repositoryMock, times(1)).findOne(dto.getId());
        verifyNoMoreInteractions(repositoryMock);

        assertEquals(dto.getId(), actual.getId());
        assertEquals(dto.getDescription(), actual.getDescription());
        assertEquals(dto.getTitle(), actual.getTitle());
    }

    @Test(expected = TodoNotFoundException.class)
    public void updateWhenToDoIsNotFound() throws TodoNotFoundException {
        TodoDTO dto = TodoTestUtil.createDTO(TodoTestUtil.ID, TodoTestUtil.DESCRIPTION_UPDATED, TodoTestUtil.TITLE_UPDATED);
        when(repositoryMock.findOne(dto.getId())).thenReturn(null);

        service.update(dto);

        verify(repositoryMock, times(1)).findOne(dto.getId());
        verifyNoMoreInteractions(repositoryMock);
    }
}
