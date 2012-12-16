package net.petrikainulainen.spring.testmvc.security.authentication;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Petri Kainulainen
 */
public class RestAuthenticationFailureHandler implements AuthenticationFailureHandler {

    protected static final String STATUS_MESSAGE_AUTHENTICATION_FAILED = "Bad credentials";

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, STATUS_MESSAGE_AUTHENTICATION_FAILED);
    }
}
