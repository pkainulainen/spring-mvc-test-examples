package net.petrikainulainen.springmvctest.junit5.web;

import net.petrikainulainen.springmvctest.junit5.todo.CreateTodoItemDTO;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;

import static net.petrikainulainen.springmvctest.junit5.web.WebTestConfig.jsonMapper;
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
     * @param input    The information of the created todo item
     * @return  A {@link ResultActions} object that allows us to write assertions for the HTTP response.
     * @throws Exception    if an error occurs.
     */
    ResultActions create(CreateTodoItemDTO input) throws Exception {
        return mockMvc.perform(post("/todo-item")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(input))
        );
    }

    private static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        var mapper = jsonMapper();
        return mapper.writeValueAsBytes(object);
    }

    /**
     * Creates and sends the HTTP requests which gets the
     * HTML document that displays the information of all todo items.
     * @return  A {@link ResultActions} object that allows us to write assertions for the HTTP response.
     * @throws Exception    if an error occurs.
     */
    ResultActions findAll() throws Exception {
        return mockMvc.perform(get("/todo-item"));
    }

    /**
     * Creates and sends the HTTP request which gets the
     * HTML document that displays the information of the
     * requested todo item.
     * @param id    The id of the requested todo item.
     * @return  A {@link ResultActions} object that allows us to write assertions for the HTTP response.
     * @throws Exception    if an error occurs.
     */
    ResultActions findById(Long id) throws Exception {
        return mockMvc.perform(get("/todo-item/{id}", id));
    }
}
