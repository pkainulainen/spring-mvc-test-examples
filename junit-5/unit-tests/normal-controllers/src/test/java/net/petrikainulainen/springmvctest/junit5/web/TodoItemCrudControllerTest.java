package net.petrikainulainen.springmvctest.junit5.web;

import net.petrikainulainen.springmvctest.junit5.todo.TodoItemCrudService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static net.petrikainulainen.springmvctest.junit5.web.WebTestConfig.*;
import static org.mockito.Mockito.mock;

class TodoItemCrudControllerTest {

    private TodoItemRequestBuilder requestBuilder;
    private TodoItemCrudService service;

    @BeforeEach
    void configureSystemUnderTest() {
        service = mock(TodoItemCrudService.class);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new TodoItemCrudController(service))
                .setHandlerExceptionResolvers(exceptionResolver())
                .setLocaleResolver(fixedLocaleResolver())
                .setViewResolvers(jspViewResolver())
                .build();
        requestBuilder = new TodoItemRequestBuilder(mockMvc);
    }

    @Test
    @DisplayName("Should configure system under test")
    void shouldConfigureSystemUnderTest() {
        //Left blank on purpose
    }
}
