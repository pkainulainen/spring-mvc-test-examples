package net.petrikainulainen.springmvctest.junit5.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.petrikainulainen.springmvctest.junit5.todo.CreateTodoItemDTO;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.util.Map;

import static net.petrikainulainen.springmvctest.junit5.web.WebTestConfig.objectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Creates and sends the HTTP requests which are used to write unit tests
 * for controllers methods which provide CRUD operations for todo items.
 */
class TodoItemRequestBuilder {

    static final String TEMPLATE_VARIABLE_DESCRIPTION = "description";
    static final String TEMPLATE_VARIABLE_TITLE = "title";

    private final MockMvc mockMvc;
    private final TemplateEngine templateEngine;

    TodoItemRequestBuilder(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.templateEngine = new TemplateEngine();
    }

    /**
     * Creates and sends an HTTP request which creates new todo items.
     *
     * @param requestBodyTemplate   The template that specifies the structure of the request body.
     * @param templateVariables     The variables which are used to build the actual request body
     *                              that's send to the system under test. The keys of this map must be
     *                              equal to the variable names which are used in the Thymeleaf
     *                              template.
     * @return  A {@link ResultActions} object which allows us to write assertions for the
     *          returned response.
     */
    ResultActions create(RequestBodyTemplate requestBodyTemplate, Map<String, Object> templateVariables) throws Exception {
        var requestBody = buildRequestBody(requestBodyTemplate, templateVariables);
        return mockMvc.perform(post("/todo-item")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

    }
    private String buildRequestBody(RequestBodyTemplate template, Map<String, Object> variables) {
        var context = new Context();
        var variableKeys = variables.keySet();
        for (String key: variableKeys) {
            context.setVariable(key, variables.get(key));
        }

        return this.templateEngine.process(template.getTemplate(), context);
    }

    /**
     * Creates and sends an HTTP request which fetches all todo items found from
     * the database.
     *
     * @return  A {@link ResultActions} object which allows us to write assertions for the
     *          returned response.
     */
    ResultActions findAll() throws Exception {
        return mockMvc.perform(get("/todo-item"));
    }

    /**
     * Creates and sends an HTTP request which fetches the information of the specified
     * todo item.
     *
     * @param id    The id of the requested todo item.
     * @return  A {@link ResultActions} object which allows us to write assertions for the
     *          returned response.
     */
    ResultActions findById(Long id) throws Exception {
        return mockMvc.perform(get("/todo-item/{id}", id));
    }

    /**
     * Specifies the different request body templates which are supported
     * by our request builder class. We should use an enum instead of just
     * passing a string to our request builder methods because:
     * <ul>
     *     <li>
     *          Using an enum nsures that our request builder is still responsible
     *          of building the request body by using the information provided by
     *          the user our request builder. If our request builder methods would take
     *          the tamplete as a {@link String} object, the user of our request builder
     *          would be responsible of building parts of the request (the request body).
     *     </li>
     *     <li>
     *         Keeping our request body templates in one place makes them easier to
     *         maintain. If we have to make changes to these templates, we don't have to
     *         look our templates from different test classes. We can simply open our request
     *         builder class and make the required changes to the template. However, we might
     *         still have make changes to the template variables which are configured in our
     *         test classes.
     *     </li>
     * </ul>
     */
    enum RequestBodyTemplate {

        EMPTY_TODO_ITEM("{}"),
        TODO_ITEM(
                """
               {
                "description":"[[${description}]]",
                "title":"[[${title}]]"
               }
                """
        );

        private final String template;

        private RequestBodyTemplate(String template) {
            this.template = template;
        }

        /**
         * Returns the Thymeleaf template that can be used for building the
         * actual request body.
         */
        String getTemplate() {
            return template;
        }
    }
}
