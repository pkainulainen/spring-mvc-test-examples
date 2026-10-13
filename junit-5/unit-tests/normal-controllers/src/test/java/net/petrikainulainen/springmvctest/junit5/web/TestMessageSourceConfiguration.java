package net.petrikainulainen.springmvctest.junit5.web;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.StaticMessageSource;

/**
 * Ensures that our slice tests use a <code>StaticMessageSource</code>
 * that allows our tests to configure the required code-message pairs.
 */
@TestConfiguration
class TestMessageSourceConfiguration {

    @Bean
    public StaticMessageSource messageSource() {
        return new StaticMessageSource();
    }
}
