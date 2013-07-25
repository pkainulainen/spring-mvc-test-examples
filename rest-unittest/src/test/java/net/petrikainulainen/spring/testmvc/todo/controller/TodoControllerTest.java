package net.petrikainulainen.spring.testmvc.todo.controller;

import net.petrikainulainen.spring.testmvc.config.WebAppContext;
import net.petrikainulainen.spring.testmvc.todo.TestUtil;
import net.petrikainulainen.spring.testmvc.config.TestContext;
import net.petrikainulainen.spring.testmvc.todo.dto.TodoDTO;
import net.petrikainulainen.spring.testmvc.todo.dto.TodoDTOBuilder;
import net.petrikainulainen.spring.testmvc.todo.exception.TodoNotFoundException;
import net.petrikainulainen.spring.testmvc.todo.model.Todo;
import net.petrikainulainen.spring.testmvc.todo.model.TodoBuilder;
import net.petrikainulainen.spring.testmvc.todo.service.TodoService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Petri Kainulainen
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestContext.class, WebAppContext.class})
//@ContextConfiguration(locations = {"classpath:testContext.xml", "classpath:exampleApplicationContext-web.xml"})
@WebAppConfiguration
public class TodoControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private TodoService todoServiceMock;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        //We have to reset our mock between tests because the mock objects
        //are managed by the Spring container. If we would not reset them,
        //stubbing and verified behavior would "leak" from one test to another.
        Mockito.reset(todoServiceMock);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void add_EmptyTodoEntry_ShouldReturnValidationErrorForTitle() throws Exception {
        TodoDTO dto = new TodoDTO();

        mockMvc.perform(post("/api/todo")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dto))
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.fieldErrors", hasSize(1)))
                .andExpect(jsonPath("$.fieldErrors[0].path", is("title")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("The title cannot be empty.")));

        verifyZeroInteractions(todoServiceMock);
    }

    @Test
    public void add_TitleAndDescriptionAreTooLong_ShouldReturnValidationErrorsForTitleAndDescription() throws Exception {
        String title = TestUtil.createStringWithLength(Todo.MAX_LENGTH_TITLE + 1);
        String description = TestUtil.createStringWithLength(Todo.MAX_LENGTH_DESCRIPTION + 1);

        TodoDTO dto = new TodoDTOBuilder()
                .description(description)
                .title(title)
                .build();

        mockMvc.perform(post("/api/todo")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dto))
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.fieldErrors", hasSize(2)))
                .andExpect(jsonPath("$.fieldErrors[*].path", containsInAnyOrder("title", "description")))
                .andExpect(jsonPath("$.fieldErrors[*].message", containsInAnyOrder(
                        "The maximum length of the description is 500 characters.",
                        "The maximum length of the title is 100 characters."
                )));

        verifyZeroInteractions(todoServiceMock);
    }

    @Test
    public void add_NewTodoEntry_ShouldAddTodoEntryAndReturnAddedEntry() throws Exception {
        TodoDTO dto = new TodoDTOBuilder()
                .description("description")
                .title("title")
                .build();

        Todo added = new TodoBuilder()
                .id(1L)
                .description("description")
                .title("title")
                .build();

        when(todoServiceMock.add(any(TodoDTO.class))).thenReturn(added);

        mockMvc.perform(post("/api/todo")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dto))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.title", is("title")));

        ArgumentCaptor<TodoDTO> dtoCaptor = ArgumentCaptor.forClass(TodoDTO.class);
        verify(todoServiceMock, times(1)).add(dtoCaptor.capture());
        verifyNoMoreInteractions(todoServiceMock);

        TodoDTO dtoArgument = dtoCaptor.getValue();
        assertNull(dtoArgument.getId());
        assertThat(dtoArgument.getDescription(), is("description"));
        assertThat(dtoArgument.getTitle(), is("title"));
    }

    @Test
    public void deleteById_TodoEntryFound_ShouldDeleteTodoEntryAndReturnIt() throws Exception {
        Todo deleted = new TodoBuilder()
                .id(1L)
                .description("Lorem ipsum")
                .title("Foo")
                .build();

        when(todoServiceMock.deleteById(1L)).thenReturn(deleted);

        mockMvc.perform(delete("/api/todo/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Lorem ipsum")))
                .andExpect(jsonPath("$.title", is("Foo")));

        verify(todoServiceMock, times(1)).deleteById(1L);
        verifyNoMoreInteractions(todoServiceMock);
    }

    @Test
    public void deleteById_TodoIsNotFound_ShouldReturnHttpStatusCode404() throws Exception {
        when(todoServiceMock.deleteById(3L)).thenThrow(new TodoNotFoundException(""));

        mockMvc.perform(delete("/api/todo/{id}", 3L))
                .andExpect(status().isNotFound());

        verify(todoServiceMock, times(1)).deleteById(3L);
        verifyNoMoreInteractions(todoServiceMock);
    }

    @Test
    public void findAll_TodosFound_ShouldReturnFoundTodoEntries() throws Exception {
        Todo first = new TodoBuilder()
                .id(1L)
                .description("Lorem ipsum")
                .title("Foo")
                .build();
        Todo second = new TodoBuilder()
                .id(2L)
                .description("Lorem ipsum")
                .title("Bar")
                .build();

        when(todoServiceMock.findAll()).thenReturn(Arrays.asList(first, second));

        mockMvc.perform(get("/api/todo"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("Lorem ipsum")))
                .andExpect(jsonPath("$[0].title", is("Foo")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].description", is("Lorem ipsum")))
                .andExpect(jsonPath("$[1].title", is("Bar")));

        verify(todoServiceMock, times(1)).findAll();
        verifyNoMoreInteractions(todoServiceMock);
    }

    @Test
    public void findById_TodoEntryFound_ShouldReturnFoundTodoEntry() throws Exception {
        Todo found = new TodoBuilder()
                .id(1L)
                .description("Lorem ipsum")
                .title("Foo")
                .build();

        when(todoServiceMock.findById(1L)).thenReturn(found);

        mockMvc.perform(get("/api/todo/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Lorem ipsum")))
                .andExpect(jsonPath("$.title", is("Foo")));

        verify(todoServiceMock, times(1)).findById(1L);
        verifyNoMoreInteractions(todoServiceMock);
    }

    @Test
    public void findById_TodoEntryNotFound_ShouldReturnHttpStatusCode404() throws Exception {
        when(todoServiceMock.findById(1L)).thenThrow(new TodoNotFoundException(""));

        mockMvc.perform(get("/api/todo/{id}", 1L))
                .andExpect(status().isNotFound());

        verify(todoServiceMock, times(1)).findById(1L);
        verifyNoMoreInteractions(todoServiceMock);
    }

    @Test
    public void update_EmptyTodoEntry_ShouldReturnValidationErrorForTitle() throws Exception {
        TodoDTO dto = new TodoDTOBuilder()
                .id(1L)
                .build();

        mockMvc.perform(put("/api/todo/{id}", 1L)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dto))
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.fieldErrors", hasSize(1)))
                .andExpect(jsonPath("$.fieldErrors[0].path", is("title")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("The title cannot be empty.")));

        verifyZeroInteractions(todoServiceMock);
    }

    @Test
    public void update_TitleAndDescriptionAreTooLong_ShouldReturnValidationErrorsForTitleAndDescription() throws Exception {
        String title = TestUtil.createStringWithLength(Todo.MAX_LENGTH_TITLE + 1);
        String description = TestUtil.createStringWithLength(Todo.MAX_LENGTH_DESCRIPTION + 1);

        TodoDTO dto = new TodoDTOBuilder()
                .id(1L)
                .description(description)
                .title(title)
                .build();

        mockMvc.perform(put("/api/todo/{id}", 1L)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dto))
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.fieldErrors", hasSize(2)))
                .andExpect(jsonPath("$.fieldErrors[*].path", containsInAnyOrder("title", "description")))
                .andExpect(jsonPath("$.fieldErrors[*].message", containsInAnyOrder(
                        "The maximum length of the description is 500 characters.",
                        "The maximum length of the title is 100 characters."
                )));

        verifyZeroInteractions(todoServiceMock);
    }

    @Test
    public void update_TodoEntryNotFound_ShouldReturnHttpStatusCode404() throws Exception {
        TodoDTO dto = new TodoDTOBuilder()
                .id(3L)
                .description("description")
                .title("title")
                .build();

        when(todoServiceMock.update(any(TodoDTO.class))).thenThrow(new TodoNotFoundException(""));

        mockMvc.perform(put("/api/todo/{id}", 3L)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dto))
        )
                .andExpect(status().isNotFound());

        ArgumentCaptor<TodoDTO> dtoCaptor = ArgumentCaptor.forClass(TodoDTO.class);
        verify(todoServiceMock, times(1)).update(dtoCaptor.capture());
        verifyNoMoreInteractions(todoServiceMock);

        TodoDTO dtoArgument = dtoCaptor.getValue();
        assertThat(dtoArgument.getId(), is(3L));
        assertThat(dtoArgument.getDescription(), is("description"));
        assertThat(dtoArgument.getTitle(), is("title"));
    }

    @Test
    public void update_TodoEntryFound_ShouldUpdateTodoEntryAndReturnIt() throws Exception {
        TodoDTO dto = new TodoDTOBuilder()
                .id(1L)
                .description("description")
                .title("title")
                .build();

        Todo updated = new TodoBuilder()
                .id(1L)
                .description("description")
                .title("title")
                .build();

        when(todoServiceMock.update(any(TodoDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/todo/{id}", 1L)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dto))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.title", is("title")));

        ArgumentCaptor<TodoDTO> dtoCaptor = ArgumentCaptor.forClass(TodoDTO.class);
        verify(todoServiceMock, times(1)).update(dtoCaptor.capture());
        verifyNoMoreInteractions(todoServiceMock);

        TodoDTO dtoArgument = dtoCaptor.getValue();
        assertThat(dtoArgument.getId(), is(1L));
        assertThat(dtoArgument.getDescription(), is("description"));
        assertThat(dtoArgument.getTitle(), is("title"));
    }
}
