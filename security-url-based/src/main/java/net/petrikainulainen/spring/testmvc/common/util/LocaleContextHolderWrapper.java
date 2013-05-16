package net.petrikainulainen.spring.testmvc.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * @author Petri Kainulainen
 */
@Component
public class LocaleContextHolderWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocaleContextHolderWrapper.class);

    public Locale getCurrentLocale() {
        LOGGER.debug("Getting current locale");
        return LocaleContextHolder.getLocale();
    }
}
