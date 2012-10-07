package net.petrikainulainen.spring.testmvc;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Petri Kainulainen
 */
public class ApplicationContextRule implements TestRule {

    private static final Map<String, MockMvc> mockMvcCache = new ConcurrentHashMap<String, MockMvc>();

    Object test;

    public ApplicationContextRule(Object test) {
        this.test = test;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        ApplicationContextSetup contextConfiguration = description.getTestClass().getAnnotation(ApplicationContextSetup.class);
        if (contextConfiguration != null) {
            MockMvc mockMvc = configureMockMvc(contextConfiguration);
            injectMockMvcToTestClass(description.getTestClass(), mockMvc);
        }

        return new ApplicationContextStatement(base);
    }

    private MockMvc configureMockMvc(ApplicationContextSetup contextConfiguration) {
        if (configurationClassIsNotGiven(contextConfiguration) && configurationFileIsNotGiven(contextConfiguration)) {
            throw new IllegalArgumentException("You have to provide either application context configuration class or configuration file!");
        }

        String cacheKey = getCacheKey(contextConfiguration);
        MockMvc mockMvc = mockMvcCache.get(cacheKey);

        if (mockMvc == null) {
            if (configurationFileIsNotGiven(contextConfiguration)) {
                mockMvc = MockMvcBuilders.annotationConfigSetup(contextConfiguration.configurationClass()).build();
            }
            else {
                mockMvc = MockMvcBuilders.xmlConfigSetup(contextConfiguration.configurationFile()).build();
            }

            mockMvcCache.put(cacheKey, mockMvc);
        }

        return mockMvc;

    }

    private String getCacheKey(ApplicationContextSetup contextConfiguration) {
        String cacheKey;

        if (configurationFileIsNotGiven(contextConfiguration)) {
            cacheKey = contextConfiguration.configurationClass().getCanonicalName();
        }
        else {
            cacheKey = contextConfiguration.configurationFile();
        }

        return cacheKey;
    }

    private boolean configurationClassIsNotGiven(ApplicationContextSetup contextSetup) {
        return contextSetup.configurationClass().isInstance(Empty.class);
    }

    private boolean configurationFileIsNotGiven(ApplicationContextSetup contextSetup) {
        String configurationFile = contextSetup.configurationFile();
        return (configurationFile == null) || configurationFile.isEmpty();
    }

    private void injectMockMvcToTestClass(Class testClass, MockMvc mockMvc) {
        Field[] fields = testClass.getDeclaredFields();
        for (Field field: fields) {
            if (field.getType().equals(MockMvc.class)) {
                try {
                    field.setAccessible(true);
                    Object currentMockMvc = field.get(test);
                    if (currentMockMvc == null) {
                        field.set(test, mockMvc);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("The MockMvc object could not be injected to field: " + field.getName());
                }
                break;
            }
        }
    }

    public class ApplicationContextStatement extends Statement {

        private final Statement statement;

        public ApplicationContextStatement(Statement statement) {
            this.statement = statement;
        }

        @Override
        public void evaluate() throws Throwable {
            statement.evaluate();
        }
    }
}
