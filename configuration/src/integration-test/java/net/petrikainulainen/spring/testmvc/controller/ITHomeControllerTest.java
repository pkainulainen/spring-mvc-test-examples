package net.petrikainulainen.spring.testmvc.controller;

import net.petrikainulainen.spring.testmvc.config.ExampleApplicationContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.view;

/**
 * This test uses the annotation based application context configuration.
 * @author Petri Kainulainen
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = org.springframework.test.web.server.samples.context.WebContextLoader.class, classes = {ExampleApplicationContext.class})
//@ContextConfiguration(loader = WebContextLoader.class, locations = {"classpath:exampleApplicationContext.xml"})
public class ITHomeControllerTest {

    @Resource
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webApplicationContextSetup(webApplicationContext).build();
    }

    @Test
    public void showHomePageWhenClassLevelConfigurationIsUsed() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name(HomeController.VIEW_HOME_PAGE));
    }

    @Test
    public void showHomePageWhenMethodLevelConfigurationIsUsed() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name(HomeController.VIEW_HOME_PAGE));
    }
}
