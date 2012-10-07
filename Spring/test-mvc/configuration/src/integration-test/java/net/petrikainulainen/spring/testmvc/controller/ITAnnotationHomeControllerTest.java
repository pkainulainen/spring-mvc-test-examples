package net.petrikainulainen.spring.testmvc.controller;

import net.petrikainulainen.spring.testmvc.ApplicationContextRule;
import net.petrikainulainen.spring.testmvc.ApplicationContextSetup;
import net.petrikainulainen.spring.testmvc.config.ExampleApplicationContext;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.test.web.server.MockMvc;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.view;

/**
 * This test uses the annotation based application context configuration.
 * @author Petri Kainulainen
 */
@ApplicationContextSetup(configurationClass = ExampleApplicationContext.class)
public class ITAnnotationHomeControllerTest {

    @Rule
    public ApplicationContextRule rule = new ApplicationContextRule(this);

    private MockMvc mockMvc;

    @Test
    public void showHomePage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name(HomeController.VIEW_HOME_PAGE));
    }
}
