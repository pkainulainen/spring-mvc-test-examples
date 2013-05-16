package net.petrikainulainen.spring.testmvc.security.authentication;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;

/**
 * @author Petri Kainulainen
 */
public class RestAuthenticationEntryPointTest {

    private RestAuthenticationEntryPoint authenticationEntryPoint;

    @Before
    public void setUp() {
        authenticationEntryPoint = new RestAuthenticationEntryPoint();
    }

    @Test
    public void commence() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthenticationException ex = new AuthenticationCredentialsNotFoundException("");

        authenticationEntryPoint.commence(request, response, ex);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }
}
