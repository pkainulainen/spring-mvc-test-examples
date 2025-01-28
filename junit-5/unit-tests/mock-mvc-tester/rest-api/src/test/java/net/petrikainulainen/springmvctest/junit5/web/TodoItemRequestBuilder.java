package net.petrikainulainen.springmvctest.junit5.web;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

/**
 * Creates and sends the HTTP requests which are used to write unit tests
 * for controllers methods which provide CRUD operations for todo items.
 */
class TodoItemRequestBuilder {

    static final String TEMPLATE_VARIABLE_DESCRIPTION = "description";
    static final String TEMPLATE_VARIABLE_TITLE = "title";
    static final String TEMPLATE_VARIABLE_UNKNOWN  = "unknown";

    private final MockMvcTester mockMvc;
    private final TemplateEngine templateEngine;

    TodoItemRequestBuilder(MockMvc mockMvc) {
        this.mockMvc = MockMvcTester.create(mockMvc);
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
     * @return  A {@link MvcTestResult} object which allows us to write assertions for the
     *          returned response with AssertJ.
     */
    MvcTestResult create(RequestBodyTemplate requestBodyTemplate, Map<String, Object> templateVariables) {
        var requestBody = buildRequestBody(requestBodyTemplate, templateVariables);
        return mockMvc.post()
                .uri("/todo-item")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();
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
     * @return  A {@link MvcTestResult} object which allows us to write assertions for the
     *          returned response with AssertJ.
     */
    MvcTestResult findAll() {
        return mockMvc.get()
                .uri("/todo-item")
                .exchange();
    }

    /**
     * Creates and sends an HTTP request which fetches the information of the specified
     * todo item.
     *
     * @param id    The id of the requested todo item.
     * @return  A {@link MvcTestResult} object which allows us to write assertions for the
     *          returned response with AssertJ.
     */
    MvcTestResult findById(Long id) {
        return mockMvc.get()
                .uri("/todo-item/{id}", id)
                .exchange();
    }

    /**
     * Specifies the different request body templates which are supported
     * by our request builder class. We should use an enum instead of just
     * passing a string to our request builder method because:
     * <ul>
     *     <li>
     *          If we use an enum, we ensure that our request builder is responsible for building
     *          the request body by using the information provided by the user of our request builder.
     *          If our request builder method would take the template as a {@link String} object, the
     *          user of our request builder would be responsible for building a part of the request
     *          (the request body).
     *     </li>
     *     <li>
     *         If we keep our request body templates in one place, they are easier to maintain. If we have
     *         to make changes to these templates, we don't have to search our templates from different test
     *         classes. We can simply open our request builder class and make the required changes to the templates.
     *         However, we might still have to make changes to the template variables which are configured in our
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
        ),
        TODO_ITEM_WITH_UNKNOWN_PROPERTY(
                """
                {
                    "description":"[[${description}]]",
                    "title":"[[${title}]]",
                    "unknown":"[[${unknown}]]"
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
