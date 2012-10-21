package net.petrikainulainen.spring.testmvc.todo.service;

import net.petrikainulainen.spring.testmvc.todo.ToDoTestUtil;
import net.petrikainulainen.spring.testmvc.todo.dto.ToDoDTO;
import net.petrikainulainen.spring.testmvc.todo.exception.ToDoNotFoundException;
import net.petrikainulainen.spring.testmvc.todo.model.ToDo;
import net.petrikainulainen.spring.testmvc.todo.repository.ToDoRepository;
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
public class RepositoryToDoServiceTest {

    private RepositoryToDoService service;

    private ToDoRepository repositoryMock;

    @Before
    public void setUp() {
        service = new RepositoryToDoService();

        repositoryMock = mock(ToDoRepository.class);
        ReflectionTestUtils.setField(service, "repository", repositoryMock);
    }

    @Test
    public void add() {
        ToDoDTO dto = ToDoTestUtil.createDTO(null, ToDoTestUtil.DESCRIPTION, ToDoTestUtil.TITLE);

        service.add(dto);

        ArgumentCaptor<ToDo> toDoArgument = ArgumentCaptor.forClass(ToDo.class);
        verify(repositoryMock, times(1)).save(toDoArgument.capture());
        verifyNoMoreInteractions(repositoryMock);

        ToDo model = toDoArgument.getValue();

        assertNull(model.getId());
        assertEquals(dto.getDescription(), model.getDescription());
        assertEquals(dto.getTitle(), model.getTitle());
    }

    @Test
    public void deleteById() throws ToDoNotFoundException {
        ToDo model = ToDoTestUtil.createModel(ToDoTestUtil.ID, ToDoTestUtil.DESCRIPTION, ToDoTestUtil.TITLE);
        when(repositoryMock.findOne(ToDoTestUtil.ID)).thenReturn(model);

        ToDo actual = service.deleteById(ToDoTestUtil.ID);

        verify(repositoryMock, times(1)).findOne(ToDoTestUtil.ID);
        verify(repositoryMock, times(1)).delete(model);
        verifyNoMoreInteractions(repositoryMock);

        assertEquals(model, actual);
    }

    @Test(expected = ToDoNotFoundException.class)
    public void deleteByIdWhenToDoIsNotFound() throws ToDoNotFoundException {
        when(repositoryMock.findOne(ToDoTestUtil.ID)).thenReturn(null);

        service.deleteById(ToDoTestUtil.ID);

        verify(repositoryMock, times(1)).findOne(ToDoTestUtil.ID);
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    public void findAll() {
        List<ToDo> models = new ArrayList<ToDo>();
        when(repositoryMock.findAll()).thenReturn(models);

        List<ToDo> actual = service.findAll();

        verify(repositoryMock, times(1)).findAll();
        verifyNoMoreInteractions(repositoryMock);

        assertEquals(models, actual);
    }

    @Test
    public void findById() throws ToDoNotFoundException {
        ToDo model = ToDoTestUtil.createModel(ToDoTestUtil.ID, ToDoTestUtil.DESCRIPTION, ToDoTestUtil.TITLE);
        when(repositoryMock.findOne(ToDoTestUtil.ID)).thenReturn(model);

        ToDo actual = service.findById(ToDoTestUtil.ID);

        verify(repositoryMock, times(1)).findOne(ToDoTestUtil.ID);
        verifyNoMoreInteractions(repositoryMock);

        assertEquals(model, actual);
    }

    @Test(expected = ToDoNotFoundException.class)
    public void findByIdWhenToDoIsNotFound() throws ToDoNotFoundException {
        when(repositoryMock.findOne(ToDoTestUtil.ID)).thenReturn(null);

        service.findById(ToDoTestUtil.ID);

        verify(repositoryMock, times(1)).findOne(ToDoTestUtil.ID);
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    public void update() throws ToDoNotFoundException {
        ToDoDTO dto = ToDoTestUtil.createDTO(ToDoTestUtil.ID, ToDoTestUtil.DESCRIPTION_UPDATED, ToDoTestUtil.TITLE_UPDATED);
        ToDo model = ToDoTestUtil.createModel(ToDoTestUtil.ID, ToDoTestUtil.DESCRIPTION, ToDoTestUtil.TITLE);
        when(repositoryMock.findOne(dto.getId())).thenReturn(model);

        ToDo actual = service.update(dto);

        verify(repositoryMock, times(1)).findOne(dto.getId());
        verifyNoMoreInteractions(repositoryMock);

        assertEquals(dto.getId(), actual.getId());
        assertEquals(dto.getDescription(), actual.getDescription());
        assertEquals(dto.getTitle(), actual.getTitle());
    }

    @Test(expected = ToDoNotFoundException.class)
    public void updateWhenToDoIsNotFound() throws ToDoNotFoundException {
        ToDoDTO dto = ToDoTestUtil.createDTO(ToDoTestUtil.ID, ToDoTestUtil.DESCRIPTION_UPDATED, ToDoTestUtil.TITLE_UPDATED);
        when(repositoryMock.findOne(dto.getId())).thenReturn(null);

        service.update(dto);

        verify(repositoryMock, times(1)).findOne(dto.getId());
        verifyNoMoreInteractions(repositoryMock);
    }
}
