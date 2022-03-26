package net.petrikainulainen.springmvctest.junit5.web;

import net.petrikainulainen.springmvctest.junit5.todo.CreateTodoItemFormDTO;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Creates and sends the HTTP requests which are used
 * to write unit tests for controllers methods which
 * provide CRUD operations for todo items.
 */
class TodoItemRequestBuilder {

    private final MockMvc mockMvc;

    TodoItemRequestBuilder(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    /**
     * Creates and sends the HTTP requests which submit the created
     * todo item form.
     *
     * @param formObject    The information of the created todo item
     * @return
     * @throws Exception
     */
    ResultActions create(CreateTodoItemFormDTO formObject) throws Exception {
        return mockMvc.perform(post("/todo-item")
                .param("description", formObject.getDescription())
                .param("title", formObject.getTitle())
        );
    }

    /**
     * Creates and sends the HTTP requests which gets the
     * HTML document that displays the information of all todo items.
     * @return
     * @throws Exception
     */
    ResultActions findAll() throws Exception {
        return mockMvc.perform(get("/todo-item"));
    }

    /**
     * Creates and sends the HTTP request which gets the
     * HTML document that displays the information of the
     * requested todo item.
     * @param id    The id of the requested todo item.
     * @return
     * @throws Exception
     */
    ResultActions findById(Long id) throws Exception {
        return mockMvc.perform(get("/todo-item/{id}", id));
    }
}
