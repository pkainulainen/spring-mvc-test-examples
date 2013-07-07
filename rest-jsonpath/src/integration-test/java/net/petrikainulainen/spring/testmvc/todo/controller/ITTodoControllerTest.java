package net.petrikainulainen.spring.testmvc.todo.controller;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import net.petrikainulainen.spring.testmvc.*;
import net.petrikainulainen.spring.testmvc.config.ExampleApplicationContext;
import net.petrikainulainen.spring.testmvc.todo.TodoTestUtil;
import net.petrikainulainen.spring.testmvc.todo.dto.TodoDTO;
import net.petrikainulainen.spring.testmvc.todo.model.Todo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import javax.sql.DataSource;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.server.samples.context.SecurityRequestPostProcessors.userDetailsService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This test uses the annotation based application context configuration.
 * @author Petri Kainulainen
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ExampleApplicationContext.class})
//@ContextConfiguration(locations = {"classpath:exampleApplicationContext.xml"})
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class })
@DatabaseSetup("toDoData.xml")
public class ITTodoControllerTest {

    @Resource
    private FilterChainProxy springSecurityFilterChain;

    @Resource
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(springSecurityFilterChain)
                .build();
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void addAsAnonymous() throws Exception {
        TodoDTO added = TodoTestUtil.createDTO(null, "description", "title");
        mockMvc.perform(post("/api/todo")
                .contentType(IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .content(IntegrationTestUtil.convertObjectToJsonBytes(added))
        )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @ExpectedDatabase(value="toDoData-add-expected.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void addAsUser() throws Exception {
        TodoDTO added = TodoTestUtil.createDTO(null, "description", "title");
        mockMvc.perform(post("/api/todo")
                .contentType(IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .content(IntegrationTestUtil.convertObjectToJsonBytes(added))
                .with(userDetailsService(IntegrationTestUtil.CORRECT_USERNAME))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(IntegrationTestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.title", is("title")));
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void addEmptyTodoAsAnonymous() throws Exception {
        TodoDTO added = TodoTestUtil.createDTO(null, "", "");
        mockMvc.perform(post("/api/todo")
                .contentType(IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .content(IntegrationTestUtil.convertObjectToJsonBytes(added))
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void addEmptyTodoAsUser() throws Exception {
        TodoDTO added = TodoTestUtil.createDTO(null, "", "");
        mockMvc.perform(post("/api/todo")
                .contentType(IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .content(IntegrationTestUtil.convertObjectToJsonBytes(added))
                .with(userDetailsService(IntegrationTestUtil.CORRECT_USERNAME))
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(IntegrationTestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.fieldErrors", hasSize(1)))
                .andExpect(jsonPath("$.fieldErrors[0].path", is("title")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("The title cannot be empty.")));
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void addTodoWhenTitleAndDescriptionAreTooLongAsAnonymous() throws Exception {
        String title = TodoTestUtil.createStringWithLength(Todo.MAX_LENGTH_TITLE + 1);
        String description = TodoTestUtil.createStringWithLength(Todo.MAX_LENGTH_DESCRIPTION + 1);
        TodoDTO added = TodoTestUtil.createDTO(null, description, title);

        mockMvc.perform(post("/api/todo")
                .contentType(IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .content(IntegrationTestUtil.convertObjectToJsonBytes(added))
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void addTodoWhenTitleAndDescriptionAreTooLongAsUser() throws Exception {
        String title = TodoTestUtil.createStringWithLength(Todo.MAX_LENGTH_TITLE + 1);
        String description = TodoTestUtil.createStringWithLength(Todo.MAX_LENGTH_DESCRIPTION + 1);
        TodoDTO added = TodoTestUtil.createDTO(null, description, title);

        mockMvc.perform(post("/api/todo")
                .contentType(IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .content(IntegrationTestUtil.convertObjectToJsonBytes(added))
                .with(userDetailsService(IntegrationTestUtil.CORRECT_USERNAME))
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(IntegrationTestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.fieldErrors", hasSize(2)))
                .andExpect(jsonPath("$.fieldErrors[*].path", containsInAnyOrder("title", "description")))
                .andExpect(jsonPath("$.fieldErrors[*].message", containsInAnyOrder(
                        "The maximum length of the description is 500 characters.",
                        "The maximum length of the title is 100 characters."
                )));
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void deleteByIdAsAnonymous() throws Exception {
        mockMvc.perform(delete("/api/todo/{id}", 1L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @ExpectedDatabase("toDoData-delete-expected.xml")
    public void deleteByIdAsUser() throws Exception {
        mockMvc.perform(delete("/api/todo/{id}", 1L)
                .with(userDetailsService(IntegrationTestUtil.CORRECT_USERNAME))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(IntegrationTestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Lorem ipsum")))
                .andExpect(jsonPath("$.title", is("Foo")));
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void deleteByIdWhenTodoIsNotFoundAsAnonymous() throws Exception {
        mockMvc.perform(delete("/api/todo/{id}", 3L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void deleteByIdWhenTodoIsNotFoundAsUser() throws Exception {
        mockMvc.perform(delete("/api/todo/{id}", 3L)
                .with(userDetailsService(IntegrationTestUtil.CORRECT_USERNAME))
        )
                .andExpect(status().isNotFound());
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void findAllAsAnonymous() throws Exception {
        mockMvc.perform(get("/api/todo"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void findAllAsUser() throws Exception {
        mockMvc.perform(get("/api/todo")
                .with(userDetailsService(IntegrationTestUtil.CORRECT_USERNAME))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(IntegrationTestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("Lorem ipsum")))
                .andExpect(jsonPath("$[0].title", is("Foo")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].description", is("Lorem ipsum")))
                .andExpect(jsonPath("$[1].title", is("Bar")));
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void findByIdAsAnonymous() throws Exception {
        mockMvc.perform(get("/api/todo/{id}", 1L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void findByIdAsUser() throws Exception {
        mockMvc.perform(get("/api/todo/{id}", 1L)
                .with(userDetailsService(IntegrationTestUtil.CORRECT_USERNAME))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(IntegrationTestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Lorem ipsum")))
                .andExpect(jsonPath("$.title", is("Foo")));
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void findByIdWhenTodoIsNotFoundAsAnonymous() throws Exception {
        mockMvc.perform(get("/api/todo/{id}", 3L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void findByIdWhenTodoIsNotFoundAsUser() throws Exception {
        mockMvc.perform(get("/api/todo/{id}", 3L)
                .with(userDetailsService(IntegrationTestUtil.CORRECT_USERNAME))
        )
                .andExpect(status().isNotFound());
    }

    @Test
    @ExpectedDatabase(value="toDoData.xml")
    public void updateAsAnonymous() throws Exception {
        TodoDTO updated = TodoTestUtil.createDTO(1L, "description", "title");

        mockMvc.perform(put("/api/todo/{id}", 1L)
                .contentType(IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .content(IntegrationTestUtil.convertObjectToJsonBytes(updated))
        )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @ExpectedDatabase(value="toDoData-update-expected.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void updateAsUser() throws Exception {
        TodoDTO updated = TodoTestUtil.createDTO(1L, "description", "title");

        mockMvc.perform(put("/api/todo/{id}", 1L)
                .contentType(IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .content(IntegrationTestUtil.convertObjectToJsonBytes(updated))
                .with(userDetailsService(IntegrationTestUtil.CORRECT_USERNAME))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(IntegrationTestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.title", is("title")));
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void updateEmptyTodoAsAnonymous() throws Exception {
        TodoDTO updated = TodoTestUtil.createDTO(1L, "", "");

        mockMvc.perform(put("/api/todo/{id}", 1L)
                .contentType(IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .content(IntegrationTestUtil.convertObjectToJsonBytes(updated))
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void updateEmptyTodoAsUser() throws Exception {
        TodoDTO updated = TodoTestUtil.createDTO(1L, "", "");

        mockMvc.perform(put("/api/todo/{id}", 1L)
                .contentType(IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .content(IntegrationTestUtil.convertObjectToJsonBytes(updated))
                .with(userDetailsService(IntegrationTestUtil.CORRECT_USERNAME))
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(IntegrationTestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.fieldErrors", hasSize(1)))
                .andExpect(jsonPath("$.fieldErrors[0].path", is("title")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("The title cannot be empty.")));
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void updateTodoWhenTitleAndDescriptionAreTooLongAsAnonymous() throws Exception {
        String title = TodoTestUtil.createStringWithLength(Todo.MAX_LENGTH_TITLE + 1);
        String description = TodoTestUtil.createStringWithLength(Todo.MAX_LENGTH_DESCRIPTION + 1);

        TodoDTO updated = TodoTestUtil.createDTO(1L, description, title);

        mockMvc.perform(put("/api/todo/{id}", 1L)
                .contentType(IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .content(IntegrationTestUtil.convertObjectToJsonBytes(updated))
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void updateTodoWhenTitleAndDescriptionAreTooLongAsUser() throws Exception {
        String title = TodoTestUtil.createStringWithLength(Todo.MAX_LENGTH_TITLE + 1);
        String description = TodoTestUtil.createStringWithLength(Todo.MAX_LENGTH_DESCRIPTION + 1);

        TodoDTO updated = TodoTestUtil.createDTO(1L, description, title);

        mockMvc.perform(put("/api/todo/{id}", 1L)
                .contentType(IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .content(IntegrationTestUtil.convertObjectToJsonBytes(updated))
                .with(userDetailsService(IntegrationTestUtil.CORRECT_USERNAME))
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(IntegrationTestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.fieldErrors", hasSize(2)))
                .andExpect(jsonPath("$.fieldErrors[*].path", containsInAnyOrder("title", "description")))
                .andExpect(jsonPath("$.fieldErrors[*].message", containsInAnyOrder(
                        "The maximum length of the description is 500 characters.",
                        "The maximum length of the title is 100 characters."
                )));
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void updateTodoWhenTodoIsNotFoundAsAnonymous() throws Exception {
        TodoDTO updated = TodoTestUtil.createDTO(3L, "description", "title");

        mockMvc.perform(put("/api/todo/{id}", 3L)
                .contentType(IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .content(IntegrationTestUtil.convertObjectToJsonBytes(updated))
        )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void updateTodoWhenTodoIsNotFoundAsUser() throws Exception {
        TodoDTO updated = TodoTestUtil.createDTO(3L, "description", "title");

        mockMvc.perform(put("/api/todo/{id}", 3L)
                .contentType(IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .content(IntegrationTestUtil.convertObjectToJsonBytes(updated))
                .with(userDetailsService(IntegrationTestUtil.CORRECT_USERNAME))
        )
                .andExpect(status().isNotFound());
    }
}

