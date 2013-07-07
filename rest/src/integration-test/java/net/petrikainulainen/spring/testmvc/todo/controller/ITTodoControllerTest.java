package net.petrikainulainen.spring.testmvc.todo.controller;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import net.petrikainulainen.spring.testmvc.IntegrationTestUtil;
import net.petrikainulainen.spring.testmvc.config.ExampleApplicationContext;
import net.petrikainulainen.spring.testmvc.todo.TodoTestUtil;
import net.petrikainulainen.spring.testmvc.todo.dto.TodoDTO;
import net.petrikainulainen.spring.testmvc.todo.model.Todo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.samples.context.WebContextLoader;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import javax.sql.DataSource;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

/**
 * This test uses the annotation based application context configuration.
 * @author Petri Kainulainen
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = WebContextLoader.class, classes = {ExampleApplicationContext.class})
//@ContextConfiguration(loader = WebContextLoader.class, locations = {"classpath:exampleApplicationContext.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class })
@DatabaseSetup("toDoData.xml")
public class ITTodoControllerTest {

    @Resource
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webApplicationContextSetup(webApplicationContext).build();
    }

    @Test
    @ExpectedDatabase(value="toDoData-add-expected.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void add() throws Exception {
        TodoDTO added = TodoTestUtil.createDTO(null, "description", "title");
        mockMvc.perform(post("/api/todo")
                .contentType(IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .body(IntegrationTestUtil.convertObjectToJsonBytes(added))
        )
                .andExpect(status().isOk())
                .andExpect(content().mimeType(IntegrationTestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string("{\"id\":3,\"description\":\"description\",\"title\":\"title\"}"));
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void addEmptyTodo() throws Exception {
        TodoDTO added = TodoTestUtil.createDTO(null, "", "");
        mockMvc.perform(post("/api/todo")
                .contentType(IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .body(IntegrationTestUtil.convertObjectToJsonBytes(added))
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().mimeType(IntegrationTestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string("{\"fieldErrors\":[{\"path\":\"title\",\"message\":\"The title cannot be empty.\"}]}"));
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void addTodoWhenTitleAndDescriptionAreTooLong() throws Exception {
        String title = TodoTestUtil.createStringWithLength(Todo.MAX_LENGTH_TITLE + 1);
        String description = TodoTestUtil.createStringWithLength(Todo.MAX_LENGTH_DESCRIPTION + 1);
        TodoDTO added = TodoTestUtil.createDTO(null, description, title);

        mockMvc.perform(post("/api/todo")
                .contentType(IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .body(IntegrationTestUtil.convertObjectToJsonBytes(added))
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().mimeType(IntegrationTestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string(startsWith("{\"fieldErrors\":[")))
                .andExpect(content().string(allOf(
                        containsString("{\"path\":\"description\",\"message\":\"The maximum length of the description is 500 characters.\"}"),
                        containsString("{\"path\":\"title\",\"message\":\"The maximum length of the title is 100 characters.\"}")
                )))
                .andExpect(content().string(endsWith("]}")));
    }

    @Test
    @ExpectedDatabase("toDoData-delete-expected.xml")
    public void deleteById() throws Exception {
        mockMvc.perform(delete("/api/todo/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().mimeType(IntegrationTestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string("{\"id\":1,\"description\":\"Lorem ipsum\",\"title\":\"Foo\"}"));
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void deleteByIdWhenTodoIsNotFound() throws Exception {
        mockMvc.perform(delete("/api/todo/{id}", 3L))
                .andExpect(status().isNotFound());
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void findAll() throws Exception {
        mockMvc.perform(get("/api/todo"))
                .andExpect(status().isOk())
                .andExpect(content().mimeType(IntegrationTestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string("[{\"id\":1,\"description\":\"Lorem ipsum\",\"title\":\"Foo\"},{\"id\":2,\"description\":\"Lorem ipsum\",\"title\":\"Bar\"}]"));
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void findById() throws Exception {
        mockMvc.perform(get("/api/todo/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().mimeType(IntegrationTestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string("{\"id\":1,\"description\":\"Lorem ipsum\",\"title\":\"Foo\"}"));
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void findByIdWhenTodoIsNotFound() throws Exception {
        mockMvc.perform(get("/api/todo/{id}", 3L))
                .andExpect(status().isNotFound());
    }

    @Test
    @ExpectedDatabase(value="toDoData-update-expected.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void update() throws Exception {
        TodoDTO updated = TodoTestUtil.createDTO(1L, "description", "title");

        mockMvc.perform(put("/api/todo/{id}", 1L)
                .contentType(IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .body(IntegrationTestUtil.convertObjectToJsonBytes(updated))
        )
                .andExpect(status().isOk())
                .andExpect(content().mimeType(IntegrationTestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string("{\"id\":1,\"description\":\"description\",\"title\":\"title\"}"));
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void updateEmptyTodo() throws Exception {
        TodoDTO updated = TodoTestUtil.createDTO(1L, "", "");

        mockMvc.perform(put("/api/todo/{id}", 1L)
                .contentType(IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .body(IntegrationTestUtil.convertObjectToJsonBytes(updated))
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().mimeType(IntegrationTestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string("{\"fieldErrors\":[{\"path\":\"title\",\"message\":\"The title cannot be empty.\"}]}"));
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void updateTodoWhenTitleAndDescriptionAreTooLong() throws Exception {
        String title = TodoTestUtil.createStringWithLength(Todo.MAX_LENGTH_TITLE + 1);
        String description = TodoTestUtil.createStringWithLength(Todo.MAX_LENGTH_DESCRIPTION + 1);

        TodoDTO updated = TodoTestUtil.createDTO(1L, description, title);

        mockMvc.perform(put("/api/todo/{id}", 1L)
                .contentType(IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .body(IntegrationTestUtil.convertObjectToJsonBytes(updated))
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().mimeType(IntegrationTestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string(startsWith("{\"fieldErrors\":[")))
                .andExpect(content().string(allOf(
                        containsString("{\"path\":\"description\",\"message\":\"The maximum length of the description is 500 characters.\"}"),
                        containsString("{\"path\":\"title\",\"message\":\"The maximum length of the title is 100 characters.\"}")
                )))
                .andExpect(content().string(endsWith("]}")));
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void updateTodoWhenTodoIsNotFound() throws Exception {
        TodoDTO updated = TodoTestUtil.createDTO(3L, "description", "title");

        mockMvc.perform(put("/api/todo/{id}", 3L)
                .contentType(IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .body(IntegrationTestUtil.convertObjectToJsonBytes(updated))
        )
                .andExpect(status().isNotFound());
    }
}

