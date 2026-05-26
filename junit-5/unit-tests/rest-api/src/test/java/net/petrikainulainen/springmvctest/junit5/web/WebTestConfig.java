package net.petrikainulainen.springmvctest.junit5.web;

import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import tools.jackson.databind.json.JsonMapper;

/**
 * This class provides static factory methods which are used
 * to create and configure the Spring MVC infrastructure components
 * which we can use when we write unit tests for a Spring MVC
 * REST API.
 */
public final class WebTestConfig {

    /**
     * Prevents instantiation.
     */
    private WebTestConfig() {}

    /**
     * Creates a new {@code HttpMessageConverter} object that can read and
     * write JSON documents by using Jackson's {@link JsonMapper}.
     */
    public static JacksonJsonHttpMessageConverter objectMapperHttpMessageConverter() {
        return new JacksonJsonHttpMessageConverter(jsonMapper());
    }

    /**
     * Creates a new {@link JsonMapper} object. This method is public
     * because our request builder classes will invoke this method when
     * they have to send JSON documents to our REST API. THis ensures
     * that our request builder classes and the Spring MVC Test framework
     * use the same {@link JsonMapper} configuration.
     */
   private static JsonMapper jsonMapper() {
        return new JsonMapper();
    }
}

