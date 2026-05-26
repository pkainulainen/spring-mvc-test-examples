package net.petrikainulainen.springmvctest.junit5.web;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Creates and sends the HTTP requests which are used to write unit tests for controllers methods which
 * provide CRUD operations for todo items.
 */
class TodoItemRequestBuilder {

    static String TEMPLATE_VARIABLE_DESCRIPTION = "description";
    static String TEMPLATE_VARIABLE_TITLE = "title";

    private final MockMvc mockMvc;
    private final TemplateEngine templateEngine;

    TodoItemRequestBuilder(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.templateEngine = new TemplateEngine();
    }

    /**
     * Creates and sends an HTTP request which creates a new todo item.
     *
     * @param requestBodyTemplate   A template that defines the structure of the request body.
     * @param templateVariables     A map that contains the values that will be used to replace the placeholders found
     *                              from the request body template.
     * @return  A {@link ResultActions} object that allows us to write assertions for the HTTP response.
     * @throws Exception    if an error occurs.
     */
    ResultActions create(CreateRequestBodyTemplate requestBodyTemplate,
                         Map<String, Object> templateVariables) throws Exception {
        var requestBody = buildRequestBody(requestBodyTemplate, templateVariables);
        return mockMvc.perform(post("/todo-item")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );
    }

    private String buildRequestBody(CreateRequestBodyTemplate template,
                                    Map<String, Object> variables) {
        var context = new Context();
        var variableKeys = variables.keySet();

        for (String key: variableKeys) {
            context.setVariable(key, variables.get(key));
        }

        return this.templateEngine.process(template.getTemplate(), context);
    }

    /**
     * Creates and sends an HTTP request which queries all todo items found from the database.
     *
     * @return  A {@link ResultActions} object that allows us to write assertions for the HTTP response.
     * @throws Exception    if an error occurs.
     */
    ResultActions findAll() throws Exception {
        return mockMvc.perform(get("/todo-item"));
    }

    /**
     * Creates and sends an HTTP request which queries the information of the specified todo item.
     *
     * @param id    The id of the requested todo item.
     * @return  A {@link ResultActions} object that allows us to write assertions for the HTTP response.
     * @throws Exception    if an error occurs.
     */
    ResultActions findById(Long id) throws Exception {
        return mockMvc.perform(get("/todo-item/{id}", id));
    }

    enum CreateRequestBodyTemplate {
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

        CreateRequestBodyTemplate(String template) {
            this.template = template;
        }

        String getTemplate() {
            return template;
        }
    }
}
