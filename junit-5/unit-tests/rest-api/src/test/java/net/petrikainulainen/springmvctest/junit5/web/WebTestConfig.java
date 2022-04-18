package net.petrikainulainen.springmvctest.junit5.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import java.util.Locale;
import java.util.Properties;

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
     * write JSON documents by using Jackson's {@code ObjectMapper}.
     *
     * @return
     */
    public static MappingJackson2HttpMessageConverter objectMapperHttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper());
        return converter;
    }

    /**
     * Creates a new {@code ObjectMapper} object. This method is public
     * because our request builder classes will invoke this method when
     * they have to send JSON documents to our REST API. THis ensures
     * that our request builder classes and the Spring MVC Test framework
     * use the same {@code ObjectMapper} configuration.
     *
     * @return
     */
    public static ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}

