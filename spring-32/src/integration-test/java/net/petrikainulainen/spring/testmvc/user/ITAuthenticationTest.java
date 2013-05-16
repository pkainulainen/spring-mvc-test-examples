package net.petrikainulainen.spring.testmvc.user;

import net.petrikainulainen.spring.testmvc.IntegrationTestUtil;
import net.petrikainulainen.spring.testmvc.config.ExampleApplicationContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.samples.context.SecurityRequestPostProcessors.userDetailsService;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Petri Kainulainen
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ExampleApplicationContext.class})
//@ContextConfiguration(locations = {"classpath:exampleApplicationContext.xml"})
@WebAppConfiguration
public class ITAuthenticationTest {

    @Resource
    private FilterChainProxy springSecurityFilterChain;

    @Resource
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(springSecurityFilterChain)
                .build();
    }

    @Test
    public void loginWithCorrectCredentials() throws Exception {
        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param(IntegrationTestUtil.REQUEST_PARAMETER_USERNAME, IntegrationTestUtil.CORRECT_USERNAME)
                .param(IntegrationTestUtil.REQUEST_PARAMETER_PASSWORD, IntegrationTestUtil.CORRECT_PASSWORD)
        )
                .andExpect(status().isOk());
    }

    @Test
    public void loginWithIncorrectCredentials() throws Exception {
        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param(IntegrationTestUtil.REQUEST_PARAMETER_USERNAME, IntegrationTestUtil.INCORRECT_USERNAME)
                .param(IntegrationTestUtil.REQUEST_PARAMETER_PASSWORD, IntegrationTestUtil.INCORRECT_PASSWORD)
        )
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void loginByUsingIncorrectRequestMethod() throws Exception {
        mockMvc.perform(get("/api/login")
                .param(IntegrationTestUtil.REQUEST_PARAMETER_USERNAME, IntegrationTestUtil.CORRECT_USERNAME)
                .param(IntegrationTestUtil.REQUEST_PARAMETER_PASSWORD, IntegrationTestUtil.CORRECT_PASSWORD)
        )
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void logout() throws Exception {
        mockMvc.perform(get("/api/logout")
                .with(userDetailsService(IntegrationTestUtil.CORRECT_USERNAME))
        )
                .andExpect(status().isOk());
    }
}
