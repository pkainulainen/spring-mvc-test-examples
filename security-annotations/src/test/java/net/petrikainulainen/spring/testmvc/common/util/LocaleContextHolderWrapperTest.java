package net.petrikainulainen.spring.testmvc.common.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * @author Petri Kainulainen
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({LocaleContextHolder.class})
public class LocaleContextHolderWrapperTest {

    private LocaleContextHolderWrapper localeContextHolderWrapper;

    @Before
    public void setUp() {
        localeContextHolderWrapper = new LocaleContextHolderWrapper();
    }

    @Test
    public void getCurrentLocale() {
        PowerMockito.mockStatic(LocaleContextHolder.class);
        when(LocaleContextHolder.getLocale()).thenReturn(Locale.US);

        Locale currentLocale = localeContextHolderWrapper.getCurrentLocale();

        PowerMockito.verifyStatic(times(1));
        LocaleContextHolder.getLocale();

        assertEquals(Locale.US, currentLocale);
    }
}
