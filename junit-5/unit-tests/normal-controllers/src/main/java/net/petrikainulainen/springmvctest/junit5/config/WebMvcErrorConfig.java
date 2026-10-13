package net.petrikainulainen.springmvctest.junit5.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import java.util.List;
import java.util.Properties;

@Configuration
public class WebMvcErrorConfig implements WebMvcConfigurer {

    public static final String VIEW_NAME_NOT_FOUND_VIEW = "error/404";
    public static final String VIEW_NAME_ERROR_VIEW = "error/500";

    public static final String HTTP_STATUS_CODE_NOT_FOUND = "404";
    public static final String HTTP_STATUS_CODE_INTERNAL_SERVER_ERROR = "500";

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        SimpleMappingExceptionResolver exceptionResolver = new SimpleMappingExceptionResolver();

        Properties exceptionMappings = new Properties();
        exceptionMappings.put("net.petrikainulainen.springmvctest.junit5.todo.TodoItemNotFoundException", VIEW_NAME_NOT_FOUND_VIEW);
        exceptionMappings.put("java.lang.RuntimeException", VIEW_NAME_ERROR_VIEW);
        exceptionMappings.put("java.lang.Exception", VIEW_NAME_ERROR_VIEW);

        exceptionResolver.setExceptionMappings(exceptionMappings);

        Properties statusCodes = new Properties();
        statusCodes.put(VIEW_NAME_NOT_FOUND_VIEW, HTTP_STATUS_CODE_NOT_FOUND);
        statusCodes.put(VIEW_NAME_ERROR_VIEW, HTTP_STATUS_CODE_INTERNAL_SERVER_ERROR);

        exceptionResolver.setStatusCodes(statusCodes);

        exceptionResolver.setOrder(Ordered.HIGHEST_PRECEDENCE);
        resolvers.add(0, exceptionResolver);
    }
}