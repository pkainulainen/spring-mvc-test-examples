package net.petrikainulainen.spring.testmvc.controller;

import org.junit.Test;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.view;

/**
 * @author Petri Kainulainen
 */
public class ITStandaloneHomeControllerTest {

    @Test
    public void showHomePage() throws Exception {
        MockMvcBuilders.standaloneSetup(new HomeController()).build()
                .perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name(HomeController.VIEW_HOME_PAGE));
    }
}
