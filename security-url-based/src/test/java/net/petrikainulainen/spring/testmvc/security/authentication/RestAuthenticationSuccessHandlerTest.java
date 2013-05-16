package net.petrikainulainen.spring.testmvc.security.authentication;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;

/**
 * @author Petri Kainulainen
 */
public class RestAuthenticationSuccessHandlerTest {

    private RestAuthenticationSuccessHandler successHandler;

    @Before
    public void setUp() {
        successHandler = new RestAuthenticationSuccessHandler();
    }

    @Test
    public void onAuthenticationSuccess() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, null);

        successHandler.onAuthenticationSuccess(request, response, authentication);

        assertEquals(MockHttpServletResponse.SC_OK, response.getStatus());
    }
}
