package net.petrikainulainen.spring.testmvc.todo.service;

import net.petrikainulainen.spring.testmvc.todo.TestUtil;
import net.petrikainulainen.spring.testmvc.todo.dto.TodoDTOBuilder;
import net.petrikainulainen.spring.testmvc.todo.dto.TodoDTO;
import net.petrikainulainen.spring.testmvc.todo.exception.TodoNotFoundException;
import net.petrikainulainen.spring.testmvc.todo.model.Todo;
import net.petrikainulainen.spring.testmvc.todo.model.TodoBuilder;
import net.petrikainulainen.spring.testmvc.todo.repository.TodoRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Petri Kainulainen
 */
public class RepositoryTodoServiceTest {

    public static final Long ID = 1L;
    public static final String DESCRIPTION = "description";
    public static final String DESCRIPTION_UPDATED = "updatedDescription";
    public static final String TITLE = "title";
    public static final String TITLE_UPDATED = "updatedTitle";

    private RepositoryTodoService service;

    private TodoRepository repositoryMock;

    @Before
    public void setUp() {
        repositoryMock = mock(TodoRepository.class);
        service = new RepositoryTodoService(repositoryMock);
    }

    @Test
    public void add_NewTodoEntry_ShouldSaveTodoEntry() {
        TodoDTO dto = new TodoDTOBuilder()
                .description(DESCRIPTION)
                .title(TITLE)
                .build();

        service.add(dto);

        ArgumentCaptor<Todo> toDoArgument = ArgumentCaptor.forClass(Todo.class);
        verify(repositoryMock, times(1)).save(toDoArgument.capture());
        verifyNoMoreInteractions(repositoryMock);

        Todo model = toDoArgument.getValue();

        assertNull(model.getId());
        assertThat(model.getDescription(), is(dto.getDescription()));
        assertThat(model.getTitle(), is(dto.getTitle()));
    }

    @Test
    public void deleteById_TodoEntryFound_ShouldDeleteTodoEntryAndReturnIt() throws TodoNotFoundException {
        Todo model = new TodoBuilder()
                .id(ID)
                .description(DESCRIPTION)
                .title(TITLE)
                .build();

        when(repositoryMock.findOne(ID)).thenReturn(model);

        Todo actual = service.deleteById(ID);

        verify(repositoryMock, times(1)).findOne(ID);
        verify(repositoryMock, times(1)).delete(model);
        verifyNoMoreInteractions(repositoryMock);

        assertThat(actual, is(model));
    }

    @Test(expected = TodoNotFoundException.class)
    public void deleteById_TodoEntryNotFound_ShouldThrowException() throws TodoNotFoundException {
        when(repositoryMock.findOne(ID)).thenReturn(null);

        service.deleteById(ID);

        verify(repositoryMock, times(1)).findOne(ID);
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    public void findAll_ShouldReturnListOfTodoEntries() {
        List<Todo> models = new ArrayList<>();
        when(repositoryMock.findAll()).thenReturn(models);

        List<Todo> actual = service.findAll();

        verify(repositoryMock, times(1)).findAll();
        verifyNoMoreInteractions(repositoryMock);

        assertThat(actual, is(models));
    }

    @Test
    public void findById_TodoEntryFound_ShouldReturnFoundTodoEntry() throws TodoNotFoundException {
        Todo model = new TodoBuilder()
                .id(ID)
                .description(DESCRIPTION)
                .title(TITLE)
                .build();

        when(repositoryMock.findOne(ID)).thenReturn(model);

        Todo actual = service.findById(ID);

        verify(repositoryMock, times(1)).findOne(ID);
        verifyNoMoreInteractions(repositoryMock);

        assertThat(actual, is(model));
    }

    @Test(expected = TodoNotFoundException.class)
    public void findById_TodoEntryNotFound_ShouldThrowException() throws TodoNotFoundException {
        when(repositoryMock.findOne(ID)).thenReturn(null);

        service.findById(ID);

        verify(repositoryMock, times(1)).findOne(ID);
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    public void update_TodoEntryFound_ShouldUpdateTodoEntry() throws TodoNotFoundException {
        TodoDTO dto = new TodoDTOBuilder()
                .id(ID)
                .description(DESCRIPTION_UPDATED)
                .title(TITLE_UPDATED)
                .build();

        Todo model = new TodoBuilder()
                .id(ID)
                .description(DESCRIPTION)
                .title(TITLE)
                .build();

        when(repositoryMock.findOne(dto.getId())).thenReturn(model);

        Todo actual = service.update(dto);

        verify(repositoryMock, times(1)).findOne(dto.getId());
        verifyNoMoreInteractions(repositoryMock);

        assertThat(model.getId(), is(dto.getId()));
        assertThat(model.getDescription(), is(dto.getDescription()));
        assertThat(model.getTitle(), is(dto.getTitle()));
    }

    @Test(expected = TodoNotFoundException.class)
    public void update_TodoEntryNotFound_ShouldThrowException() throws TodoNotFoundException {
        TodoDTO dto =  new TodoDTOBuilder()
                .id(ID)
                .description(DESCRIPTION_UPDATED)
                .title(TITLE_UPDATED)
                .build();

        when(repositoryMock.findOne(dto.getId())).thenReturn(null);

        service.update(dto);

        verify(repositoryMock, times(1)).findOne(dto.getId());
        verifyNoMoreInteractions(repositoryMock);
    }
}
