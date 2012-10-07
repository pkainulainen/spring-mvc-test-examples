package net.petrikainulainen.spring.testmvc.controller;

import net.petrikainulainen.spring.testmvc.SpringTestMvcRule;
import net.petrikainulainen.spring.testmvc.ApplicationContextSetup;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.test.web.server.MockMvc;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.view;

/**
 * This test uses the xml based application context configuration.
 * @author Petri Kainulainen
 */
@ApplicationContextSetup(configurationFile = "classpath:exampleApplicationContext.xml")
public class ITXmlHomeControllerTest {

    @Rule
    public SpringTestMvcRule rule = new SpringTestMvcRule(this);

    private MockMvc mockMvc;

    @Test
    public void showHomePageWhenClassLevelConfigurationIsUsed() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name(HomeController.VIEW_HOME_PAGE));
    }

    @Test
    @ApplicationContextSetup(configurationFile = "classpath:exampleApplicationContext.xml")
    public void showHomePageWhenMethodLevelConfigurationIsUsed() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name(HomeController.VIEW_HOME_PAGE));
    }
}
