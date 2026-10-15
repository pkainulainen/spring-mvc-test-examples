package net.petrikainulainen.springmvctest.junit5.web;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static net.petrikainulainen.springmvctest.junit5.web.WebTestConfig.*;

/**
 * Provides a factory method that creates and configures a new {@link org.springframework.test.web.servlet.MockMvc}
 * object.
 */
public final class MockMvcConfig {

    /**
     * Prevents instantiation.
     */
    private MockMvcConfig() {}

    /**
     * Creates and configures a new {@link org.springframework.test.web.servlet.MockMvc} object.
     * @param controller    The tested controller object.
     * @return  The created {@link org.springframework.test.web.servlet.MockMvc} object.
     */
    public static MockMvc mockMvcForController(Object controller) {
        return MockMvcBuilders.standaloneSetup(controller)
                .setHandlerExceptionResolvers(exceptionResolver())
                .setLocaleResolver(fixedLocaleResolver())
                .setViewResolvers(jspViewResolver())
                .build();
    }
}
