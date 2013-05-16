package net.petrikainulainen.spring.testmvc.user.controller;

import net.petrikainulainen.spring.testmvc.IntegrationTestUtil;
import net.petrikainulainen.spring.testmvc.config.ExampleApplicationContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.samples.context.WebContextLoader;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.server.samples.context.SecurityRequestPostProcessors.userDetailsService;

/**
 * @author Petri Kainulainen
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = WebContextLoader.class, classes = {ExampleApplicationContext.class})
//@ContextConfiguration(loader = WebContextLoader.class, locations = {"classpath:exampleApplicationContext.xml"})
public class ITUserControllerTest {

    @Resource
    private FilterChainProxy springSecurityFilterChain;

    @Resource
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webApplicationContextSetup(webApplicationContext)
                .addFilter(springSecurityFilterChain)
                .build();
    }

    @Test
    public void getLoggedInUserAsAnonymous() throws Exception {
        mockMvc.perform(get("/api/user"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    public void getLoggedInUserAsUser() throws Exception {
        mockMvc.perform(get("/api/user")
            .with(userDetailsService(IntegrationTestUtil.CORRECT_USERNAME))
        )
                .andExpect(status().isOk())
                .andExpect(content().mimeType(IntegrationTestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string("{\"username\":\"user\",\"role\":\"ROLE_USER\"}"));
    }
}
