package net.petrikainulainen.spring.testmvc.security.authentication;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;

/**
 * @author Petri Kainulainen
 */
public class RestLogoutSuccessHandlerTest {

    private RestLogoutSuccessHandler logoutSuccessHandler;

    @Before
    public void setUp() {
        logoutSuccessHandler = new RestLogoutSuccessHandler();
    }

    @Test
    public void onLogoutSuccess() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Authentication authentication = new TestingAuthenticationToken(null, null);

        logoutSuccessHandler.onLogoutSuccess(request, response, authentication);

        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }
}
